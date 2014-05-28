package com.sungard.hackathon.monster.pojo;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class FaceDataSet {

	/** the number of training faces */
	private int nTrainFaces = 0;
	/** the training face image array */
	IplImage[] trainingFaceImgArr;
	/** the test face image array */
	IplImage[] testFaceImgArr;

	/** the person number array */
	CvMat personNumTruthMat;
	/** the number of persons */
	int nPersons;
	/** the person names */
	final List<String> personNames = new ArrayList<String>();

	/** the number of eigenvalues */
	int nEigens = 0;

	/** eigenvectors */
	IplImage[] eigenVectArr;
	/** eigenvalues */
	CvMat eigenValMat;
	/** the average image */
	IplImage pAvgTrainImg;
	/** the projected training faces */
	CvMat projectedTrainFaceMat;

	public int getnTrainFaces() {
		return nTrainFaces;
	}

	public void setnTrainFaces(int nTrainFaces) {
		this.nTrainFaces = nTrainFaces;
	}

	public IplImage[] getTrainingFaceImgArr() {
		return trainingFaceImgArr;
	}

	public void setTrainingFaceImgArr(IplImage[] trainingFaceImgArr) {
		this.trainingFaceImgArr = trainingFaceImgArr;
	}

	public IplImage[] getTestFaceImgArr() {
		return testFaceImgArr;
	}

	public void setTestFaceImgArr(IplImage[] testFaceImgArr) {
		this.testFaceImgArr = testFaceImgArr;
	}

	public CvMat getPersonNumTruthMat() {
		return personNumTruthMat;
	}

	public void setPersonNumTruthMat(CvMat personNumTruthMat) {
		this.personNumTruthMat = personNumTruthMat;
	}

	public int getnPersons() {
		return nPersons;
	}

	public void setnPersons(int nPersons) {
		this.nPersons = nPersons;
	}

	public int getnEigens() {
		return nEigens;
	}

	public void setnEigens(int nEigens) {
		this.nEigens = nEigens;
	}

	public IplImage[] getEigenVectArr() {
		return eigenVectArr;
	}

	public void setEigenVectArr(IplImage[] eigenVectArr) {
		this.eigenVectArr = eigenVectArr;
	}

	public CvMat getEigenValMat() {
		return eigenValMat;
	}

	public void setEigenValMat(CvMat eigenValMat) {
		this.eigenValMat = eigenValMat;
	}

	public IplImage getpAvgTrainImg() {
		return pAvgTrainImg;
	}

	public void setpAvgTrainImg(IplImage pAvgTrainImg) {
		this.pAvgTrainImg = pAvgTrainImg;
	}

	public CvMat getProjectedTrainFaceMat() {
		return projectedTrainFaceMat;
	}

	public void setProjectedTrainFaceMat(CvMat projectedTrainFaceMat) {
		this.projectedTrainFaceMat = projectedTrainFaceMat;
	}

	public List<String> getPersonNames() {
		return personNames;
	}

}
