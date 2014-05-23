package com.sungard.hackathon.monster.service.impl;

import static org.bytedeco.javacpp.helper.opencv_legacy.cvEigenDecomposite;
import static org.bytedeco.javacpp.opencv_core.CV_STORAGE_READ;
import static org.bytedeco.javacpp.opencv_core.cvOpenFileStorage;
import static org.bytedeco.javacpp.opencv_core.cvReadByName;
import static org.bytedeco.javacpp.opencv_core.cvReadIntByName;
import static org.bytedeco.javacpp.opencv_core.cvReadStringByName;
import static org.bytedeco.javacpp.opencv_core.cvReleaseFileStorage;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_highgui.cvLoadImage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core.CvFileStorage;
import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.IplImage;

import com.sungard.hackathon.monster.pojo.FaceDataSet;
import com.sungard.hackathon.monster.service.FaceRegService;
import com.sungard.hackathon.monster.utils.Constants;
import com.sungard.hackathon.monster.utils.FileUtils;

public class FaceRegServiceImpl implements FaceRegService {

	private static final Logger LOGGER = Logger
			.getLogger(FaceRegServiceImpl.class.getName());

	public String recogize(byte[] data) {
		if (data != null) {
			String suffix = "jpg";
			String testImgName = FileUtils.genTestName(suffix);
			FileUtils.saveImage(testImgName, data);

			IplImage faceImage = cvLoadImage(testImgName,
					CV_LOAD_IMAGE_GRAYSCALE);

			FaceDataSet fds = loadFaceDB();

			float[] projectedTestFace = new float[fds.getnEigens()];
			// project the test image onto the PCA subspace
			//TODO
//			cvEigenDecomposite(faceImage, fds.getnEigens(),
//					fds.getEigenVectArr(), 0, null, fds.getpAvgTrainImg(),
//					projectedTestFace);

			float confidence = 0.0f;

			final FloatPointer pConfidence = new FloatPointer(confidence);

			int iNearest = findNearestNeighbor(fds, projectedTestFace,
					new FloatPointer(pConfidence));

			return fds.getPersonNames().get(iNearest);
		}

		return null;
	}

	private FaceDataSet loadFaceDB() {
		FaceDataSet fds = new FaceDataSet();

		List<String> personNames = new ArrayList<String>();
		LOGGER.info("loading training data");
		// open the file-storage number
		CvFileStorage fileStorage = cvOpenFileStorage(Constants.FACEDATAFILE,
				null, CV_STORAGE_READ, "UTF-8");

		if (fileStorage == null) {
			LOGGER.severe("Can't open training database file 'facedata.xml'.");
			return null;
		}

		// Load the person names.
		int nPersons = cvReadIntByName(fileStorage, null,
				Constants.FACEDATA_PERSONS, 0);

		if (nPersons == 0) {
			LOGGER.severe("No people found in the training database 'facedata.xml'.");
			return null;
		} else {
			LOGGER.info(nPersons + " persons read from the training database");
		}

		// Load each person's name.
		for (int i = 0; i < nPersons; i++) {
			String varname = Constants.FACEDATA_PERSON_NAME + (i + 1);
			String sPersonName = cvReadStringByName(fileStorage, null, varname,
					"");
			personNames.add(sPersonName);
		}
		LOGGER.info("person names: " + personNames);

		// Load the data
		int nEigens = cvReadIntByName(fileStorage, null,
				Constants.FACEDATA_EIGENS, 0);
		int nTrainFaces = cvReadIntByName(fileStorage, null,
				Constants.FACEDATA_TRAINFACES, 0);

		Pointer pointer = cvReadByName(fileStorage, null,
				Constants.FACEDATA_MAT_TRAINPERSONNUM);
		CvMat pTrainPersonNumMat = new CvMat(pointer);

		pointer = cvReadByName(fileStorage, null,
				Constants.FACEDATA_MAT_EIGENVAL);
		CvMat eigenValMat = new CvMat(pointer);

		pointer = cvReadByName(fileStorage, null,
				Constants.FACEDATA_MAT_PROJECTEDTRAINFACE);
		CvMat projectedTrainFaceMat = new CvMat(pointer);

		pointer = cvReadByName(fileStorage, null,
				Constants.FACEDATA_IMG_AVGTRAIN);

		IplImage pAvgTrainImg = new IplImage(pointer);

		IplImage[] eigenVectArr = new IplImage[nTrainFaces];
		for (int i = 0; i <= nEigens; i++) {
			String varname = Constants.FACEDATA_EIGENVECT + i;
			pointer = cvReadByName(fileStorage, null, varname);
			eigenVectArr[i] = new IplImage(pointer);
		}

		// release the file-storage interface
		cvReleaseFileStorage(fileStorage);

		fds.setPersonNames(personNames);
		fds.setnEigens(nEigens);
		fds.setnTrainFaces(nTrainFaces);
		fds.setEigenValMat(eigenValMat);
		fds.setEigenVectArr(eigenVectArr);
		fds.setProjectedTrainFaceMat(projectedTrainFaceMat);
		fds.setpAvgTrainImg(pAvgTrainImg);
		return fds;
	}

	/**
	 * Find the most likely person based on a detection. Returns the index, and
	 * stores the confidence value into pConfidence.
	 */
	private int findNearestNeighbor(FaceDataSet fds, float projectedTestFace[],
			FloatPointer pConfidencePointer) {
		double leastDistSq = Double.MAX_VALUE;
		int iNearest = 0;

		for (int iTrain = 0; iTrain < fds.getnTrainFaces(); iTrain++) {
			double distSq = 0;

			for (int i = 0; i < fds.getnEigens(); i++) {
				float projectedTrainFaceDistance = (float) fds
						.getProjectedTrainFaceMat().get(iTrain, i);

				float d_i = projectedTestFace[i] - projectedTrainFaceDistance;
				distSq += d_i * d_i;
			}

			if (distSq < leastDistSq) {
				leastDistSq = distSq;
				iNearest = iTrain;
				LOGGER.info("training face " + (iTrain + 1)
						+ " is the new best match, least squared distance: "
						+ leastDistSq);
			}
		}

		// Return the confidence level based on the Euclidean distance,
		// so that similar images should give a confidence between 0.5 to 1.0,
		// and very different images should give a confidence between 0.0 to
		// 0.5.
		float pConfidence = (float) (1.0f - Math.sqrt(leastDistSq
				/ (float) (fds.getnTrainFaces() * fds.getnEigens())) / 255.0f);
		pConfidencePointer.put(pConfidence);

		LOGGER.info("training face " + (iNearest + 1)
				+ " is the final best match, confidence " + pConfidence);
		return iNearest;
	}
}
