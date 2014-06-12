package com.sungard.hackathon.monster.service.impl;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sungard.hackathon.monster.dao.PersonDao;
import com.sungard.hackathon.monster.pojo.FaceImage;
import com.sungard.hackathon.monster.pojo.Person;
import com.sungard.hackathon.monster.service.FaceInfoService;
import com.sungard.hackathon.monster.utils.ContextUtils;
import com.sungard.hackathon.monster.utils.FileUtils;

public class FaceInfoServiceImpl implements FaceInfoService {

	Logger log = Logger.getLogger(this.getClass());

	@Override
	public Person getPerson(String personName) {
		log.info("start getPerson name:" + personName);
		Person person = null;
		if (!StringUtils.isEmpty(personName)) {
			PersonDao dao = (PersonDao) ContextUtils.getContext().getBean(
					"PersonDao");
			File personFolder = new File(
					FileUtils.getPersonWorkSpace(personName));

			if (personFolder.exists()) {
				log.info("personFolder:" + personFolder);
				try {
					for (String picname : personFolder.list()) {
						if (StringUtils.startsWith(picname, "face")) {
							byte[] img = org.apache.commons.io.FileUtils
									.readFileToByteArray(new File(personFolder
											.getCanonicalPath()
											+ File.separator + picname));
							FaceImage faceimg = new FaceImage();
							faceimg.setData(img);

							person = dao.findByName(personName);
							if (person != null) {
								person.setImage3(faceimg);
							}
						}
					}
				} catch (Exception e) {
					log.error("read file exception", e);
				}
			}
		}
		log.info("end getPerson");
		return person;
	}

}
