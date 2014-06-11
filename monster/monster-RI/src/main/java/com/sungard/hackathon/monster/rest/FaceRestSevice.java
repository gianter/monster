package com.sungard.hackathon.monster.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.EncoderException;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.html.View;
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
    PersonForm form) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        PersonDao dao = (PersonDao) ctx.getBean("PersonDao");
        FaceTrainService trainService = new FaceTrainServiceImpl();
        Person person=new Person();
        FaceImage image = new FaceImage();
        
        String email = form.getEmail();
        String name = form.getName();
        
        List<Person> personList=dao.findByNameAndEmail(name, email);
        if(personList!=null && !personList.isEmpty()){
        	person=personList.get(0);
        	image.setData(form.getFileInput());
            image.setSuffix("jpg");
            person.setImage2(image);
            dao.addImage2(person);
        }else{
        	image.setData(form.getFileInput());
            image.setSuffix("jpg");
            person.setImage1(image);
            person.setName(name);
            person.setEmail(email);
            dao.add(person);
        }
       
        //train
        if(person.getImage1()!=null && person.getImage2()!=null){
        	trainService.analysisAll(dao.findAll());
        }
        
        return Response.ok("ok").build();
    }
    
    @POST
    @Path("/login")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public View login(@MultipartForm
            PersonForm form,@Context HttpServletRequest request) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        FaceRegService faceService=new FaceRegServiceImpl();
        PersonDao dao = (PersonDao) ctx.getBean("PersonDao");
        String name=faceService.recogize(form.getFileInput());
        List<Person> personList=(List)dao.findByName(name);
        if(personList!=null && !personList.isEmpty()){
        	Person person=personList.get(0);
        	person.setImage(new org.apache.commons.codec.binary.Base64().encode(personList.get(0).getImage1().getData()));
        	request.setAttribute("person", person);
            return new View("/welcome.jsp");
        }
        
        return null;
    }
}
