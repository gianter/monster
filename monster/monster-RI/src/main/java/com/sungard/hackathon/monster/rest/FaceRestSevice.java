package com.sungard.hackathon.monster.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
        if (email != null && name != null) {
            Person person = dao.findByNameAndEmail(name, email);
            if (person != null) {
                logger.info("second register");
                image.setData(form.getFileInput());
                image.setSuffix("jpg");
                person.setImage2(image);
                dao.addImage2(person);
                vo.setStatus("1");
            } else {
                logger.info("first register");
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
        logger.info("name:"+vo.getName());
        logger.info("email:"+vo.getEmail());
        logger.info("status:"+vo.getStatus());
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
        FaceRegService faceService = new FaceRegServiceImpl();
        FaceInfoService infoService = new FaceInfoServiceImpl();
        PersonVo vo = new PersonVo();
        String name = faceService.recogize(form.getFileInput());
        Person person = infoService.getPerson(name);
        if (person != null) {
            logger.info("find person name:"+person.getName());
            vo.setName(person.getName());
            vo.setEmail(person.getEmail());
            byte[] imageByte = new org.apache.commons.codec.binary.Base64().encode(person.getImage3().getData());
            vo.setImageString(new String(imageByte));
            vo.setStatus("1");
            request.setAttribute("person", vo);
        }
        logger.info("name:"+vo.getName());
        logger.info("email:"+vo.getEmail());
        logger.info("status:"+vo.getStatus());
        logger.info("login end");
        return vo;
    }

}
