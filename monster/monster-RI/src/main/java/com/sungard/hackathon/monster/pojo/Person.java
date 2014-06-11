package com.sungard.hackathon.monster.pojo;


public class Person {

	private String name;
	private String email;
	private FaceImage image1;
	private FaceImage image2;
	private FaceImage image3;
	private byte[] image;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FaceImage getImage1() {
		return image1;
	}

	public void setImage1(FaceImage image1) {
		this.image1 = image1;
	}

	public FaceImage getImage2() {
		return image2;
	}

	public void setImage2(FaceImage image2) {
		this.image2 = image2;
	}

	public FaceImage getImage3() {
		return image3;
	}

	public void setImage3(FaceImage image3) {
		this.image3 = image3;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getByteArrayString() {
		return new String(this.image);
	}

}
