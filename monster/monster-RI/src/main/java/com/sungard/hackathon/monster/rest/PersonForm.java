/*
 * Project Bank Fee Analysis
 * Copyright Information
 * (C) 2009-2014, SunGard Inc. All rights reserved.
 * 3F, No.210 Liangjing Road, Zhangjiang High-Tech Park, Shanghai, 201203, China.
 * 
 * This document is protected by copyright. No part of this
 * document may be reproduced in any form by any means without
 * prior written authorization of SunGard.
 * 
 */
package com.sungard.hackathon.monster.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

/*
 * Changed by           Reference Number        Description
 * ----------           ----------------        ------------------------
 */

/**
 * @author Jeff.He
 *
 */
public class PersonForm {
    
    private String name;
    private String email;
    private byte[] fileInput;
    
    public PersonForm() {
    }
    
    public byte[] getFileInput() {
        return fileInput;
    }
    
    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public void setFileInput(byte[] fileInput) {
        this.fileInput = fileInput;
    }
    
    public String getName() {
        return name;
    }
    
    @FormParam("name")
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    @FormParam("email")
    public void setEmail(String email) {
        this.email = email;
    }
    
}
