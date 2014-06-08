package com.sungard.hackathon.monster.pojo;

import java.util.ArrayList;
import java.util.List;

public class Person {
    
    private String fullName;
    private String phoneNumber;
    private FaceImage image1;
    private FaceImage image2;
    private FaceImage image3;
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

    public List<FaceImage> getImages() {
        return images;
    }

    public void setImages(List<FaceImage> images) {
        this.images = images;
    }
    
}
