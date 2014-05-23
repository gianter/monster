package com.sungard.hackathon.monster.pojo;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class FaceDataSet {

	private List<String> personNames = new ArrayList<String>();

	private int nTrainFaces;

	private int nEigens;

	private IplImage[] eigenVectArr;

	private CvMat eigenValMat;

	private IplImage pAvgTrainImg;

	private CvMat projectedTrainFaceMat;

	public int getnTrainFaces() {
		return nTrainFaces;
	}

	public void setnTrainFaces(int nTrainFaces) {
		this.nTrainFaces = nTrainFaces;
	}

	public List<String> getPersonNames() {
		return personNames;
	}

	public void setPersonNames(List<String> personNames) {
		this.personNames = personNames;
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

}
