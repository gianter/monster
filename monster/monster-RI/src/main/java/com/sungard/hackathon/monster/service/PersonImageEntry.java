package com.sungard.hackathon.monster.service;

import com.sungard.hackathon.monster.pojo.Person;

public class PersonImageEntry {
	private String imageName;

	private Person person;

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
