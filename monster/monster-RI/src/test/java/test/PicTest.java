package test;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_contrib.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_highgui.cvLoadImage;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import static org.bytedeco.javacpp.opencv_highgui.imread;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;
import junit.framework.TestCase;

import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.junit.Test;

import com.sungard.hackathon.monster.utils.ImgUtil;

public class PicTest extends TestCase {

	@Test
	public void test1() {

		String imagename = "test/C1.jpg";
		// IplImage greyImage = cvLoadImage(imagename, CV_LOAD_IMAGE_GRAYSCALE);
		IplImage greyImage = cvLoadImage(imagename);

		System.out.println("The size of the original image: " + greyImage);

		cvSaveImage("test_grey" + ".jpg", greyImage);

		IplImage img = IplImage.create(380, 216, IPL_DEPTH_8U, 1);

		// cvResize(greyImage, img, CV_INTER_LINEAR);
		cvResize(greyImage, img);

		cvSaveImage("small" + ".jpg", img);

		System.out.println("The size of the image: " + img);

		float angleDegrees = -90f;
		IplImage rImg = rotateImage(img, angleDegrees);

		cvSaveImage("r1" + ".jpg", rImg);
		System.out.println("The size of the rimage: " + rImg);

		// CvMemStorage storage = CvMemStorage.create();
		// String faceconfig = this.getClass().getClassLoader()
		// .getResource("haarcascade_frontalface_alt.xml").getPath()
		// .substring(1);
		// System.out.println("faceconfig path:" + faceconfig);
		//
		// CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(
		// cvLoad(faceconfig));
		//
		// CvSeq faces = cvHaarDetectObjects(img, classifier, storage, 1.1, 3,
		// 0);
		//
		// int total = faces.total();
		//
		// System.out.println("total: " + total);
		//
		// for (int i = 0; i < total; i++) {
		//
		// CvRect r = new CvRect(cvGetSeqElem(faces, i));
		//
		// int x = r.x(), y = r.y(), w = r.width(), h = r.height();
		//
		// String strRect = String.format("CvRect(%d,%d,%d,%d)", x, y, w, h);
		// System.out.println("rect: " + strRect);
		// }

		// MatOfRect faceDetections = new MatOfRect();
		//
		// Mat image = Highgui.imread("small.jpg");
		// CascadeClassifier faceDetector = new CascadeClassifier(
		// "/sdcard/FaceDetect/haarcascade_frontalface_alt2.xml");
		//
		// faceDetector.detectMultiScale(image, faceDetections);
		// System.out.println("total: " + faceDetections.toArray().length);

	}

	private IplImage rotateImage(final IplImage src, float angleDegrees) {
		float[] m = new float[6];
		CvMat M = CvMat.create(2, 3, CV_32F);
		int w = src.width();
		int h = src.height();
		float angleRadians = angleDegrees * ((float) Math.PI / 180.0f);
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
//		sizeRotated.width(Math.round(w));
//		sizeRotated.height(Math.round(h));
		sizeRotated.width(Math.round(h));
		sizeRotated.height(Math.round(w));

		// Rotate
		IplImage imageRotated = cvCreateImage(sizeRotated, src.depth(),
				src.nChannels());

		// Transform the image
		cvGetQuadrangleSubPix(src, imageRotated, M);

		return imageRotated;
	}

	@Test
	public void test2() {
		String imagename = "test/C1.jpg";
		IplImage greyImage = cvLoadImage(imagename, CV_LOAD_IMAGE_GRAYSCALE);
		System.out.println("before flip: " + greyImage);

		IplImage destImg = IplImage.create(greyImage.width(),
				greyImage.height(), IPL_DEPTH_8U, 1);

		cvFlip(greyImage, destImg, 1);
		System.out.println("after flip: " + destImg);

		cvSaveImage("test2.jpg", destImg);
	}
	
	@Test
	public void test4(){
		String imagename = "test/C3.jpg";
		IplImage oimg = cvLoadImage(imagename, CV_LOAD_IMAGE_GRAYSCALE);
		
		IplImage[] faceImgs = ImgUtil.detectFaceImages(oimg);
		
		for(IplImage img: faceImgs){
			int i=0;
			cvSaveImage("face" + i + ".jpg", img);
			i++;
		}
	}
	
	@Test
	public void test3(){
		String imagename = "test/W3.jpg";
		IplImage oimg = cvLoadImage(imagename, CV_LOAD_IMAGE_GRAYSCALE);
		System.out.println("oimg  is:" + oimg);
		IplImage rImg = ImgUtil.rotateImage(oimg);
		System.out.println("image detection is:" + rImg);
		
		IplImage smallImg = IplImage.create(216, 384, 8,1);
		
		cvResize(rImg, smallImg, CV_INTER_LINEAR);
		System.out.println("small image is:" + smallImg);
		
//		ImgUtil.saveImage(smallImg, "small_h.jpg");
		
//		IplImage loadImg = cvLoadImage("small_h.jpg",CV_LOAD_IMAGE_GRAYSCALE);
		IplImage loadImg = smallImg;
		CvMemStorage storage = CvMemStorage.create();
		String faceconfig = this.getClass().getClassLoader()
				.getResource("haarcascade_frontalface_alt.xml").getPath()
				.substring(1);
		CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(
				cvLoad(faceconfig));

//		IplImage parsedImg = IplImage.create(loadImg.width(),
//				loadImg.height(), IPL_DEPTH_8U, 1);
//
//		cvCvtColor(loadImg, parsedImg, CV_BGR2GRAY);
		
		CvSeq faces = cvHaarDetectObjects(loadImg, classifier, storage, 1.1,
				3, 0);

		int total = faces.total();

		System.out.println("total: " + total);
		for (int i = 0; i < total; i++) {

			CvRect rect = new CvRect(cvGetSeqElem(faces, i));

			System.out.println("rect: " + rect);
			
			cvSetImageROI(loadImg, rect);

			IplImage newImg = IplImage.create(rect.width(),
					rect.height(), loadImg.depth() , loadImg.nChannels());
			
			cvCopy(loadImg, newImg);

			cvSaveImage("n1.jpg", newImg);

			IplImage finalImg = IplImage.create(200, 200, IPL_DEPTH_8U, 1);
			
			cvResize(newImg, finalImg);
			cvSaveImage("n2.jpg", finalImg);
		}
	}
}
