package com.sungard.hackathon.monster.pojo;

import java.io.Serializable;

public class FaceImage implements Serializable {
    
    private static final long serialVersionUID = 1323821823687833540L;
    
    private String suffix;
    private byte[] data;
    
    public byte[] getData() {
        return data;
    }
    
    public void setData(byte[] data) {
        this.data = data;
    }
    
    public String getSuffix() {
        return suffix;
    }
    
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    
}
