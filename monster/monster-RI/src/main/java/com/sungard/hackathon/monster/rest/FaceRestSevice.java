package com.sungard.hackathon.monster.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.sungard.hackathon.monster.dao.PersonDao;
import com.sungard.hackathon.monster.pojo.FaceImage;
import com.sungard.hackathon.monster.pojo.Person;
import com.sungard.hackathon.monster.service.FaceInfoService;
import com.sungard.hackathon.monster.service.FaceRegService;
import com.sungard.hackathon.monster.service.FaceTrainService;
import com.sungard.hackathon.monster.service.impl.FaceInfoServiceImpl;
import com.sungard.hackathon.monster.service.impl.FaceRegServiceImpl;
import com.sungard.hackathon.monster.service.impl.FaceTrainServiceImpl;
import com.sungard.hackathon.monster.utils.ContextUtils;

@Path("/mobile")
public class FaceRestSevice {
    Logger logger = Logger.getLogger(this.getClass());

    @POST
    @Path("/register")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public PersonVo register(@MultipartForm
    PersonForm form) {
        logger.info("start register");
        PersonDao dao = (PersonDao) ContextUtils.getContext().getBean("PersonDao");
        FaceTrainService trainService = new FaceTrainServiceImpl();
        FaceInfoService infoService = new FaceInfoServiceImpl();
        PersonVo vo = new PersonVo();
        FaceImage image = new FaceImage();

        String email = form.getEmail();
        String name = form.getName();
        vo.setEmail(email);
        vo.setName(name);
        vo.setStatus("-1");
        if (StringUtils.isNotEmpty(name)) {
            Person person = dao.findByNameAndEmail(name, email);
            if (person != null) {
                logger.info("second register name:" + name);
                image.setData(form.getFileInput());
                image.setSuffix("jpg");
                person.setImage2(image);
                dao.addImage2(person);
                vo.setStatus("1");
            } else {
                logger.info("first register name:" + name);
                person = new Person();
                image.setData(form.getFileInput());
                image.setSuffix("jpg");
                person.setImage1(image);
                person.setName(name);
                person.setEmail(email);
                dao.add(person);
                vo.setStatus("0");
            }

            //train
            if (person.getImage1() != null && person.getImage2() != null) {
                logger.info("training image");
                trainService.analysisAll(dao.findAll());
                Person personimage = infoService.getPerson(name);
                if (personimage != null) {
                    byte[] imageByte = new org.apache.commons.codec.binary.Base64().encode(personimage.getImage3().getData());
                    vo.setImageString(new String(imageByte));
                }
            }
        }
        logger.info("name:" + vo.getName());
        logger.info("email:" + vo.getEmail());
        logger.info("status:" + vo.getStatus());
        logger.info("register end");

        return vo;
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public PersonVo login(@MultipartForm
    PersonForm form, @Context
    HttpServletRequest request) {
        logger.info("start login");
        PersonDao dao = (PersonDao) ContextUtils.getContext().getBean("PersonDao");
        PersonVo vo = new PersonVo();
        List<Person> personList = dao.findAll();
        if (personList == null || personList.isEmpty()) {
            logger.info("there is no person in database");
            vo.setStatus("-1");
            return vo;
        }
        FaceRegService faceService = new FaceRegServiceImpl();
        FaceInfoService infoService = new FaceInfoServiceImpl();
        String name = faceService.recogize(form.getFileInput());
        Person person = infoService.getPerson(name);
        if (person != null) {
            logger.info("find person name:" + person.getName());
            vo.setName(person.getName());
            vo.setEmail(person.getEmail());
            byte[] imageByte = new org.apache.commons.codec.binary.Base64().encode(person.getImage3().getData());
            vo.setImageString(new String(imageByte));
            vo.setStatus("1");
        }
        logger.info("name:" + vo.getName());
        logger.info("email:" + vo.getEmail());
        logger.info("status:" + vo.getStatus());
        logger.info("login success");
        return vo;
    }

    @GET
    @Path("/persons")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PersonVo> findAll(@Context
    HttpServletRequest request) {
        logger.info("start findAll");
        FaceInfoService infoService = new FaceInfoServiceImpl();
        List<PersonVo> persons = new ArrayList<PersonVo>();
        PersonDao dao = (PersonDao) ContextUtils.getContext().getBean("PersonDao");
        List<Person> personList = dao.findAll();
        for (Person person : personList) {
            PersonVo vo = new PersonVo();
            if (person.getImage1() != null && person.getImage2() != null) {
                Person personimage = infoService.getPerson(person.getName());
                if (personimage != null) {
                    byte[] imageByte = new org.apache.commons.codec.binary.Base64().encode(personimage.getImage3().getData());
                    vo.setImageString(new String(imageByte));
                    vo.setName(person.getName());
                    vo.setEmail(person.getEmail());
                    persons.add(vo);
                }
            }
        }
        logger.info("login findAll");
        return persons;
    }

}
