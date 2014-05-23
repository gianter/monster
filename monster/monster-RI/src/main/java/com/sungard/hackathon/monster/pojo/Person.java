package com.sungard.hackathon.monster.pojo;

import java.util.ArrayList;
import java.util.List;

public class Person {
	private String fullName;

	private String phoneNumber;

	private List<FaceImage> images = new ArrayList<FaceImage>();

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<FaceImage> getImages() {
		return images;
	}

	public void setImages(List<FaceImage> images) {
		this.images = images;
	}

}
