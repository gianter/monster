package com.sungard.hackathon.monster.service.impl;

import static com.sungard.hackathon.monster.utils.Constants.FACEDATAFILE;
import static com.sungard.hackathon.monster.utils.Constants.FOLDER_TRAIN_IMG;
import static org.bytedeco.javacpp.helper.opencv_legacy.cvCalcEigenObjects;
import static org.bytedeco.javacpp.helper.opencv_legacy.cvEigenDecomposite;
import static org.bytedeco.javacpp.opencv_core.CV_32FC1;
import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_core.CV_L1;
import static org.bytedeco.javacpp.opencv_core.CV_STORAGE_WRITE;
import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_ITER;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_32F;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvConvertScale;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateMat;
import static org.bytedeco.javacpp.opencv_core.cvMinMaxLoc;
import static org.bytedeco.javacpp.opencv_core.cvNormalize;
import static org.bytedeco.javacpp.opencv_core.cvOpenFileStorage;
import static org.bytedeco.javacpp.opencv_core.cvReadByName;
import static org.bytedeco.javacpp.opencv_core.cvRect;
import static org.bytedeco.javacpp.opencv_core.cvReleaseFileStorage;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvResetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_core.cvTermCriteria;
import static org.bytedeco.javacpp.opencv_core.cvWrite;
import static org.bytedeco.javacpp.opencv_core.cvWriteInt;
import static org.bytedeco.javacpp.opencv_core.cvWriteString;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_highgui.cvLoadImage;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import static org.bytedeco.javacpp.opencv_legacy.CV_EIGOBJ_NO_CALLBACK;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core.CvFileStorage;
import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.CvTermCriteria;
import org.bytedeco.javacpp.opencv_core.IplImage;

import com.sungard.hackathon.monster.pojo.FaceDataSet;
import com.sungard.hackathon.monster.pojo.FaceImage;
import com.sungard.hackathon.monster.pojo.Person;
import com.sungard.hackathon.monster.service.FaceTrainService;
import com.sungard.hackathon.monster.service.PersonImageEntry;
import com.sungard.hackathon.monster.utils.Constants;
import com.sungard.hackathon.monster.utils.FileUtils;
import com.sungard.hackathon.monster.utils.ImgUtil;

public class FaceTrainServiceImpl implements FaceTrainService {

	private static final Logger LOGGER = Logger
			.getLogger(FaceTrainServiceImpl.class.getName());

	public void analysisAll(List<Person> persons) {
		FileUtils.initDirs();

		if (persons != null && persons.size() != 0) {
			// parse and save image
			List<PersonImageEntry> pies = parseAndSave(persons);

			// perform PCA
			FaceDataSet fds = doPCA(pies);

			// Store face data
			storeTrainingData(fds);
		}
	}

	private List<PersonImageEntry> parseAndSave(List<Person> persons) {
		List<PersonImageEntry> pies = new ArrayList<PersonImageEntry>();
		for (Person person : persons) {
			List<FaceImage> images = person.getImages();
			int i = 0;
			for (FaceImage img : images) {
				String orinigalName = FOLDER_TRAIN_IMG + File.separator
						+ person.getFullName() + "_" + i + "."
						+ img.getSuffix();
				FileUtils.saveImage(orinigalName, img.getData());

				IplImage greyImage = cvLoadImage(orinigalName,
						CV_LOAD_IMAGE_GRAYSCALE);

				// IplImage finalImg = ImgUtil.standardizeImage(greyImg);
				IplImage[] finalFaceImgs = ImgUtil.detectFaceImages(greyImage);

				for (int j = 0; j < finalFaceImgs.length; j++) {
					String finalImgName = FOLDER_TRAIN_IMG + File.separator
							+ person.getFullName() + "_" + i + "_face_" + j
							+ "." + img.getSuffix();

					ImgUtil.saveImage(finalFaceImgs[j], finalImgName);
					
					PersonImageEntry pie = new PersonImageEntry();
					pie.setImageName(finalImgName);
					pie.setPerson(person);
					pies.add(pie);
				}

				i++;
			}
		}

		return pies;
	}

	private FaceDataSet doPCA(List<PersonImageEntry> pies) {
		FaceDataSet fds = new FaceDataSet();

		// Load all of training face image
		List<IplImage> trainFaceImgs = new ArrayList<IplImage>();

		for (PersonImageEntry pie : pies) {
			IplImage originalface = cvLoadImage(pie.getImageName(),
					CV_LOAD_IMAGE_GRAYSCALE);
			if (originalface == null) {
				throw new RuntimeException("Can't load image from "
						+ pie.getImageName());
			}

			trainFaceImgs.add(originalface);
			fds.getPersonNames().add(pie.getPerson().getFullName());
		}

		IplImage[] trainingFaceImgArr = trainFaceImgs
				.toArray(new IplImage[] {});

		if (trainingFaceImgArr != null && trainingFaceImgArr.length != 0) {
			CvSize faceImgSize = new CvSize();

			faceImgSize.width(trainingFaceImgArr[0].width());
			faceImgSize.height(trainingFaceImgArr[0].height());

			int nTrainFaces = trainingFaceImgArr.length;

			// set the number of eigenvalues to use
			int nEigens = nTrainFaces - 1;
			CvMat eigenValMat = cvCreateMat(1, nEigens, CV_32FC1);

			LOGGER.info("allocating images for principal component analysis, using "
					+ nEigens + " eigenvalue");

			IplImage[] eigenVectArr = new IplImage[nEigens];

			for (int i = 0; i < nEigens; i++) {
				eigenVectArr[i] = cvCreateImage(faceImgSize, IPL_DEPTH_32F, 1);
			}

			// allocate the averaged image
			IplImage pAvgTrainImg = cvCreateImage(faceImgSize, IPL_DEPTH_32F, 1);

			// set the PCA termination criterion
			CvTermCriteria calcLimit = cvTermCriteria(CV_TERMCRIT_ITER,
					nEigens, 1);

			cvCalcEigenObjects(nTrainFaces, trainingFaceImgArr, eigenVectArr,
					CV_EIGOBJ_NO_CALLBACK, 0, null, calcLimit, pAvgTrainImg,
					eigenValMat.data_fl());

			cvNormalize(eigenValMat, eigenValMat, 1, 0, CV_L1, null);

			// Keep the data
			CvMat personNumTruthMat = cvCreateMat(1, // rows
					nTrainFaces, // cols
					CV_32SC1); // type, 32-bit unsigned, one channel

			for (int j1 = 0; j1 < nTrainFaces; j1++) {
				personNumTruthMat.put(0, j1, 0);
			}

			// project the training images onto the PCA subspace
			CvMat projectedTrainFaceMat = cvCreateMat(nTrainFaces, nEigens,
					CV_32FC1);

			// initialize the training face matrix
			for (int i1 = 0; i1 < nTrainFaces; i1++) {
				for (int j1 = 0; j1 < nEigens; j1++) {
					projectedTrainFaceMat.put(i1, j1, 0.0);
				}
				personNumTruthMat.put(0, i1, i1 + 1);
			}

			final FloatPointer floatPointer = new FloatPointer(nEigens);
			for (int i = 0; i < nTrainFaces; i++) {
				cvEigenDecomposite(trainingFaceImgArr[i], nEigens,
						eigenVectArr, 0, null, pAvgTrainImg, floatPointer);

				for (int j1 = 0; j1 < nEigens; j1++) {
					projectedTrainFaceMat.put(i, j1, floatPointer.get(j1));
				}
			}

			fds.setnTrainFaces(nTrainFaces);
			fds.setnPersons(nTrainFaces);
			fds.setEigenValMat(eigenValMat);
			fds.setEigenVectArr(eigenVectArr);
			fds.setnEigens(nEigens);
			fds.setpAvgTrainImg(pAvgTrainImg);
			fds.setProjectedTrainFaceMat(projectedTrainFaceMat);
			fds.setPersonNumTruthMat(personNumTruthMat);
		}

		return fds;
	}

	public void storeTrainingData(FaceDataSet fds) {

		LOGGER.info("writing facedata.xml");
		CvFileStorage fileStorage;
		int i;
		// create a file-storage interface
		fileStorage = cvOpenFileStorage("facedata.xml", // filename
				null, // memstorage
				CV_STORAGE_WRITE, // flags
				null); // encoding

		// Store the person names. Added by Shervin.
		cvWriteInt(fileStorage, // fs
				"nPersons", // name
				fds.getnPersons()); // value

		for (i = 0; i < fds.getnPersons(); i++) {
			String varname = "personName_" + (i + 1);
			cvWriteString(fileStorage, // fs
					varname, // name
					fds.getPersonNames().get(i), // string
					0); // quote
		}

		// store all the data
		cvWriteInt(fileStorage, // fs
				"nEigens", // name
				fds.getnEigens()); // value

		cvWriteInt(fileStorage, // fs
				"nTrainFaces", // name
				fds.getnTrainFaces()); // value

		cvWrite(fileStorage, // fs
				"trainPersonNumMat", // name
				fds.getPersonNumTruthMat()); // value

		cvWrite(fileStorage, // fs
				"eigenValMat", // name
				fds.getEigenValMat()); // value

		cvWrite(fileStorage, // fs
				"projectedTrainFaceMat", // name
				fds.getProjectedTrainFaceMat());

		cvWrite(fileStorage, // fs
				"avgTrainImg", // name
				fds.getpAvgTrainImg()); // value

		for (i = 0; i < fds.getnEigens(); i++) {
			String varname = "eigenVect_" + i;
			cvWrite(fileStorage, // fs
					varname, // name
					fds.getEigenVectArr()[i]); // value
		}

		// release the file-storage interface
		cvReleaseFileStorage(fileStorage);

		// // create a file-storage interface
		// CvFileStorage fileStorage = cvOpenFileStorage(FACEDATAFILE, null,
		// CV_STORAGE_WRITE, null);
		//
		// // Store the person names.
		// cvWriteInt(fileStorage, Constants.FACEDATA_PERSONS, fds
		// .getPersonNames().size());
		// for (int i = 0; i < fds.getPersonNames().size(); i++) {
		// String varname = Constants.FACEDATA_PERSON_NAME + (i + 1);
		// cvWriteString(fileStorage, varname, fds.getPersonNames().get(i), 0);
		// }
		//
		// // Store nEigens
		// cvWriteInt(fileStorage, Constants.FACEDATA_EIGENS,
		// fds.getPersonNames()
		// .size() - 1);
		//
		// // Store train face number
		// cvWriteInt(fileStorage, Constants.FACEDATA_TRAINFACES, fds
		// .getPersonNames().size());
		//
		// cvWrite(fileStorage, "trainPersonNumMat",
		// fds.getPersonNumTruthMat());
		//
		// // Store eigenValMat
		// cvWrite(fileStorage, Constants.FACEDATA_MAT_EIGENVAL,
		// fds.getEigenValMat());
		//
		// // Store eprojectedTrainFaceMat
		// cvWrite(fileStorage, Constants.FACEDATA_MAT_PROJECTEDTRAINFACE,
		// fds.getProjectedTrainFaceMat());
		//
		// // Store avgTrainImg
		// cvWrite(fileStorage, Constants.FACEDATA_IMG_AVGTRAIN,
		// fds.getpAvgTrainImg());
		// for (int i = 0; i < fds.getEigenVectArr().length; i++) {
		// IplImage image = fds.getEigenVectArr()[i];
		// String varname = Constants.FACEDATA_EIGENVECT + (i + 1);
		// cvWrite(fileStorage, varname, image);
		// }
		//
		// // release the file-storage interface
		// cvReleaseFileStorage(fileStorage);

		// store the face image
		storeEigenfaceImages(fds);
	}

	private void storeEigenfaceImages(FaceDataSet tc) {
		// Store the average image to a file
		LOGGER.info("Saving the image of the average face as 'out_averageImage.bmp'");
		cvSaveImage("out_averageImage.bmp", tc.getpAvgTrainImg());

		// Create a large image made of many eigenface images.
		// Must also convert each eigenface image to a normal 8-bit UCHAR image
		// instead of a 32-bit float image.
		int nEigens = tc.getnEigens();
		LOGGER.info("Saving the " + tc.getnEigens()
				+ " eigenvector images as 'out_eigenfaces.bmp'");

		if (nEigens > 0) {
			int COLUMNS = 8;
			int nCols = Math.min(nEigens, COLUMNS);
			int nRows = 1 + (nEigens / COLUMNS); // Put the rest on new rows.

			int w = tc.getEigenVectArr()[0].width();
			int h = tc.getEigenVectArr()[0].height();

			CvSize size = cvSize(nCols * w, nRows * h);

			final IplImage bigImg = cvCreateImage(size, IPL_DEPTH_8U, 1);

			for (int i = 0; i < nEigens; i++) {
				IplImage byteImg = converToUcharImage(tc.getEigenVectArr()[i]);
				// Paste it into the correct position.
				int x = w * (i % COLUMNS);
				int y = h * (i / COLUMNS);

				CvRect ROI = cvRect(x, y, w, h);

				cvSetImageROI(bigImg, ROI);

				cvCopy(byteImg, bigImg, null);
				cvResetImageROI(bigImg);
				cvReleaseImage(byteImg);
			}

			cvSaveImage("out_eigenfaces.bmp", bigImg);

			cvReleaseImage(bigImg);
		}
	}

	private IplImage converToUcharImage(IplImage srcImg) {
		if ((srcImg != null) && (srcImg.width() > 0 && srcImg.height() > 0)) {
			// Spread the 32bit floating point pixels to fit within 8bit pixel
			// range.
			double[] minVal = new double[1];
			double[] maxVal = new double[1];
			cvMinMaxLoc(srcImg, minVal, maxVal);
			// Deal with NaN and extreme values, since the DFT seems to give
			// some NaN results.
			if (minVal[0] < -1e30) {
				minVal[0] = -1e30;
			}
			if (maxVal[0] > 1e30) {
				maxVal[0] = 1e30;
			}
			if (maxVal[0] - minVal[0] == 0.0f) {
				maxVal[0] = minVal[0] + 0.001; // remove potential divide by
												// zero errors.
			} // Convert the format
			IplImage dstImg = cvCreateImage(
					cvSize(srcImg.width(), srcImg.height()), 8, 1);
			cvConvertScale(srcImg, dstImg, 255.0 / (maxVal[0] - minVal[0]),
					-minVal[0] * 255.0 / (maxVal[0] - minVal[0]));
			return dstImg;
		}
		return null;
	}
}
