package com.sungard.hackathon.monster.utils;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.CV_32F;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_INTER_LINEAR;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetQuadrangleSubPix;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;

import java.util.logging.Logger;

import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.CvSize;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;

public class ImgUtil {

	private static final Logger log = Logger.getLogger(ImgUtil.class.getName());

	public static IplImage[] detectFaceImages(final IplImage greyImage) {
		log.info("orinigal face picture: " + greyImage);

		IplImage resultImg = standardizeImage(greyImage);

		String faceconfig = ImgUtil.class.getClassLoader()
				.getResource(Constants.faceDectionConfig).getPath()
				.substring(1);
		CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(
				cvLoad(faceconfig));

		CvMemStorage storage = CvMemStorage.create();
		CvSeq faces = cvHaarDetectObjects(resultImg, classifier, storage, 1.1,
				3, 0);

		int total = faces.total();
		
		log.info("Founding faces: " + total);

		IplImage[] images = new IplImage[total];
		for (int i = 0; i < total; i++) {
			CvRect rect = new CvRect(cvGetSeqElem(faces, i));

			log.info("founding rect: " + rect);

			cvSetImageROI(resultImg, rect);
			IplImage faceImg = IplImage.create(rect.width(), rect.height(),
					resultImg.depth(), resultImg.nChannels());
			cvCopy(resultImg, faceImg);

			IplImage standarizeFaceImg = IplImage.create(160, 160,
					IPL_DEPTH_8U, 1);
			cvResize(faceImg, standarizeFaceImg);
			log.info("get final face image: " + standarizeFaceImg);
			images[i] = standarizeFaceImg;
		}
		
		storage.release();
		return images;
	}

	public static IplImage standardizeImage(final IplImage greyImage) {
		log.info("image before standardiztion: " + greyImage);

		int width = greyImage.width();
		int height = greyImage.height();

		IplImage resultImg = null;
		if (width > height) {
			resultImg = resize(rotateImage(greyImage));
		} else {
			resultImg = resize(greyImage);
		}
		log.info("image after standardiztion: " + resultImg);
		return resultImg;
	}

	public static IplImage resize(final IplImage originImg) {
		log.info("image before resize:" + originImg);

		IplImage img = IplImage.create(Constants.IMG_WDITH,
				Constants.IMG_HEIGHT, IPL_DEPTH_8U, 1);
		cvResize(originImg, img, CV_INTER_LINEAR);

		log.info("image after resize:" + img);
		return img;
	}

	public static void saveImage(IplImage img, String path) {
		log.info("image: " + img + " to be saved at: " + path);
		cvSaveImage(path, img);
	}

	public static IplImage rotateImage(final IplImage src) {
		log.info("image before rotation: " + src);

		float angleDegrees = -90f;
		float angleRadians = angleDegrees * ((float) Math.PI / 180.0f);

		CvMat M = CvMat.create(2, 3, CV_32F);
		float[] m = new float[6];
		int w = src.width();
		int h = src.height();
		m[0] = (float) (Math.cos(angleRadians));
		m[1] = (float) (Math.sin(angleRadians));
		m[3] = -m[1];
		m[4] = m[0];
		m[2] = w * 0.5f;
		m[5] = h * 0.5f;
		M.put(0, m[0]);
		M.put(1, m[1]);
		M.put(2, m[2]);
		M.put(3, m[3]);
		M.put(4, m[4]);
		M.put(5, m[5]);

		CvSize sizeRotated = new CvSize();
		// sizeRotated.width(Math.round(w));
		// sizeRotated.height(Math.round(h));
		sizeRotated.width(Math.round(h));
		sizeRotated.height(Math.round(w));

		// Rotate
		IplImage imageRotated = cvCreateImage(sizeRotated, src.depth(),
				src.nChannels());

		// Transform the image
		cvGetQuadrangleSubPix(src, imageRotated, M);

		log.info("image after rotation: " + imageRotated);
		return imageRotated;
	}
}
