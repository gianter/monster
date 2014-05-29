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
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.opencv_core.CvFileStorage;
import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.IplImage;

import com.sungard.hackathon.monster.pojo.FaceDataSet;
import com.sungard.hackathon.monster.service.FaceRegService;
import com.sungard.hackathon.monster.utils.Constants;
import com.sungard.hackathon.monster.utils.FileUtils;
import com.sungard.hackathon.monster.utils.ImgUtil;

public class FaceRegServiceImpl implements FaceRegService {

	private static final Logger LOGGER = Logger
			.getLogger(FaceRegServiceImpl.class.getName());

	public String recogize(byte[] data) {

		FileUtils.initDirs();

		if (data != null) {
			String suffix = "jpg";
			String testImgName = FileUtils.genTestName(suffix);
			FileUtils.saveImage(testImgName, data);

			FaceDataSet fds = loadFaceDB();

//			final IplImage[] faceImages = new IplImage[1];
			IplImage orinigalImg = cvLoadImage(testImgName, CV_LOAD_IMAGE_GRAYSCALE);
			 
			IplImage faceImages=  ImgUtil.standardizeImage(orinigalImg);
			
			float pConfidence = 0.0f;
			int nEigens = fds.getnEigens();
			IplImage pAvgTrainImg = fds.getpAvgTrainImg();
			IplImage[] eigenVectArr = fds.getEigenVectArr();

			// final FloatPointer floatPointer = new FloatPointer(nEigens);
			float[] floatPointer = new float[nEigens];

			cvEigenDecomposite(faceImages, nEigens, eigenVectArr, 0, null,
					pAvgTrainImg, floatPointer);

			int iNearest = findNearestNeighbor(fds, floatPointer,
					new FloatPointer(pConfidence));

			String personName = fds.getPersonNames().get(iNearest);
			return personName;
		}

		return null;
	}

	private FaceDataSet loadFaceDB() {
		FaceDataSet fds = new FaceDataSet();

		LOGGER.info("loading training data");
		CvMat pTrainPersonNumMat = null; // the person numbers during training
		CvFileStorage fileStorage;
		int i;

		// create a file-storage interface
		fileStorage = cvOpenFileStorage("facedata.xml", null, CV_STORAGE_READ,
				null);
		if (fileStorage == null) {
			LOGGER.severe("Can't open training database file 'facedata.xml'.");
			return null;
		}

		// Load the person names.
		fds.getPersonNames().clear(); // Make sure it starts as empty.
		int nPersons = cvReadIntByName(fileStorage, null, "nPersons", 0);
		if (nPersons == 0) {
			LOGGER.severe("No people found in the training database 'facedata.xml'.");
			return null;
		} else {
			LOGGER.info(nPersons + " persons read from the training database");
		}

		// Load each person's name.
		for (i = 0; i < nPersons; i++) {
			String sPersonName;
			String varname = "personName_" + (i + 1);
			sPersonName = cvReadStringByName(fileStorage, // fs
					null, varname, "");
			fds.getPersonNames().add(sPersonName);
		}
		LOGGER.info("person names: " + fds.getPersonNames());

		// Load the data
		int nEigens = cvReadIntByName(fileStorage, null, "nEigens", 0);
		int nTrainFaces = cvReadIntByName(fileStorage, null, "nTrainFaces", 0);
		Pointer pointer = cvReadByName(fileStorage, null, "trainPersonNumMat");
		pTrainPersonNumMat = new CvMat(pointer);

		pointer = cvReadByName(fileStorage, null, "eigenValMat");
		CvMat eigenValMat = new CvMat(pointer);

		pointer = cvReadByName(fileStorage, null, "projectedTrainFaceMat");
		CvMat projectedTrainFaceMat = new CvMat(pointer);

		pointer = cvReadByName(fileStorage, null, "avgTrainImg");
		IplImage pAvgTrainImg = new IplImage(pointer);

		IplImage[] eigenVectArr = new IplImage[nTrainFaces];
		for (i = 0; i <= nEigens; i++) {
			String varname = "eigenVect_" + i;
			pointer = cvReadByName(fileStorage, null, varname);
			eigenVectArr[i] = new IplImage(pointer);
		}

		// release the file-storage interface
		cvReleaseFileStorage(fileStorage);

		LOGGER.info("Training data loaded (" + nTrainFaces
				+ " training images of " + nPersons + " people)");
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("People: ");
		
		fds.setnEigens(nEigens);
		fds.setnTrainFaces(nTrainFaces);
		fds.setnPersons(nPersons);
		fds.setpAvgTrainImg(pAvgTrainImg);
		fds.setEigenVectArr(eigenVectArr);
		fds.setEigenValMat(eigenValMat);
		fds.setProjectedTrainFaceMat(projectedTrainFaceMat);
		fds.setPersonNumTruthMat(pTrainPersonNumMat);

		return fds;
	}

	private int findNearestNeighbor(FaceDataSet fds, float[] projectedTestFace,
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

			LOGGER.info("face " + (iTrain) + " has squared distance: " + distSq);

			if (distSq < leastDistSq) {
				leastDistSq = distSq;
				iNearest = iTrain;
				LOGGER.info("training face " + (iTrain)
						+ " is the new best match, least squared distance: "
						+ leastDistSq);
			}

		}

		// Return the confidence level based on the Euclidean distance,
		// so that similar images should give a confidence between 0.5 to 1.0,
		// and very different images should give a confidence between 0.0 to
		// 0.5.
		LOGGER.info("leastDistSq: " + leastDistSq);

		float pConfidence = (float) (1.0f - Math.sqrt(leastDistSq
				/ (float) (fds.getnTrainFaces() * fds.getnEigens())) / 255.0f);

		LOGGER.info("pConfidence: " + pConfidence);

		// pConfidencePointer.put(pConfidence);

		LOGGER.info("training face " + (iNearest)
				+ " is the final best match, confidence " + pConfidence);
		return iNearest;
	}
}
