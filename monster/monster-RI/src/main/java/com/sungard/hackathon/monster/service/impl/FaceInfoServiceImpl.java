package com.sungard.hackathon.monster.service.impl;

import java.io.File;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.sungard.hackathon.monster.dao.PersonDao;
import com.sungard.hackathon.monster.pojo.FaceImage;
import com.sungard.hackathon.monster.pojo.Person;
import com.sungard.hackathon.monster.service.FaceInfoService;
import com.sungard.hackathon.monster.utils.ContextUtils;
import com.sungard.hackathon.monster.utils.FileUtils;

public class FaceInfoServiceImpl implements FaceInfoService {

	private static final Logger log = Logger
			.getLogger(FaceInfoServiceImpl.class.getName());

	@Override
	public Person getPerson(String personName) {

		Person person = null;
		if (!StringUtils.isEmpty(personName)) {
			PersonDao dao = (PersonDao) ContextUtils.getContext().getBean(
					"PersonDao");

			File personFolder = new File(
					FileUtils.getPersonWorkSpace(personName));
			
			if (personFolder.exists()) {
				try {
					for (String picname : personFolder.list()) {
						if (StringUtils.startsWith(picname, "face")) {
							byte[] img = org.apache.commons.io.FileUtils
									.readFileToByteArray(new File(picname));
							FaceImage faceimg = new FaceImage();
							faceimg.setData(img);
							
							person = dao.findByName(personName).get(0);
							person.setImage1(faceimg);
						}
					}
				} catch (Exception e) {
					log.info("read file exception");
				}
			}
		}
		return person;
	}

}
