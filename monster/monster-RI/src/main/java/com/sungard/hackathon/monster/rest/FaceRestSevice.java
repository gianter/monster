package com.sungard.hackathon.monster.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sungard.hackathon.monster.dao.PersonDao;
import com.sungard.hackathon.monster.pojo.FaceImage;
import com.sungard.hackathon.monster.pojo.Person;
import com.sungard.hackathon.monster.service.FaceRegService;
import com.sungard.hackathon.monster.service.FaceTrainService;
import com.sungard.hackathon.monster.service.impl.FaceRegServiceImpl;
import com.sungard.hackathon.monster.service.impl.FaceTrainServiceImpl;

@Path("/mobile")
public class FaceRestSevice {
    
    @POST
    @Path("/register")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response register(@MultipartForm
    PersonFrom form) {
        Person person = new Person();
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        PersonDao dao = (PersonDao) ctx.getBean("PersonDao");
        FaceTrainService trainService = new FaceTrainServiceImpl();
        String email = form.getEmail();
        String fullName = form.getFullName();
        FaceImage image = new FaceImage();
        
        //        String fileName = getFileName(input);
        
        image.setData(form.getFileInput());
        image.setSuffix("jpg");
        person.setImage1(image);
        
        person.setFullName(fullName);
        person.setEmail(email);
        person.getImages().add(image);
        person.setPhoneNumber("123");
        dao.add(person);
        trainService.analysisAll(dao.findAll());
        return Response.ok("ok").build();
    }
    
    @POST
    @Path("/login")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public PersonVo login(@MultipartForm
            PersonFrom form) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        FaceRegService faceService=new FaceRegServiceImpl();
        PersonDao dao = (PersonDao) ctx.getBean("PersonDao");
        String name=faceService.recogize(form.getFileInput());
        List<Person> personList=(List)dao.findByName(name);
        if(personList!=null && !personList.isEmpty()){
            PersonVo vo=new PersonVo();
            vo.setEmail(personList.get(0).getEmail());
            vo.setName(personList.get(0).getFullName());
            return vo;
        }
        
        return null;
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
