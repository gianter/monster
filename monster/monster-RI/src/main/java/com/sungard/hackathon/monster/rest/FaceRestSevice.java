package com.sungard.hackathon.monster.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.sungard.hackathon.monster.pojo.FaceImage;
import com.sungard.hackathon.monster.pojo.Person;

public class FaceRestSevice {
    @Path("/register")
    @POST
    @Consumes("multipart/form-data")
    public String register(MultipartFormDataInput input, HttpServletRequest request) {
        Person person = new Person();
        List<FaceImage> images = new ArrayList<FaceImage>();
        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");
        Map<String, List<InputPart>> personForm = input.getFormDataMap();
        List<InputPart> inputParts = personForm.get("face");
        for (InputPart inputPart : inputParts) {
            try {
                FaceImage image = new FaceImage();
                MultivaluedMap<String, String> header = inputPart.getHeaders();
                String fileName = getFileName(header); 
                //convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                image.setData(bytes);
                image.setSuffix(fileName.substring(fileName.indexOf(".")));
                images.add(image);
                //constructs upload file path
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        person.setFullName(fullName);
        person.setPhoneNumber(phoneNumber);
        person.setImages(images);
        return "";
    }
    
    @Path("/login")
    @POST
    @Consumes("multipart/form-data")
    public String login(MultipartFormDataInput input, HttpServletRequest request) {
        Person person = new Person();
        List<FaceImage> images = new ArrayList<FaceImage>();
        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");
        Map<String, List<InputPart>> personForm = input.getFormDataMap();
        List<InputPart> inputParts = personForm.get("face");
        for (InputPart inputPart : inputParts) {
            try {
                FaceImage image = new FaceImage();
                MultivaluedMap<String, String> header = inputPart.getHeaders();
                String fileName = getFileName(header); 
                //convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                image.setData(bytes);
                image.setSuffix(fileName.substring(fileName.indexOf(".")));
                images.add(image);
                //constructs upload file path
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        person.setFullName(fullName);
        person.setPhoneNumber(phoneNumber);
        person.setImages(images);
        return "";
    }
    
    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "";
    }
}
