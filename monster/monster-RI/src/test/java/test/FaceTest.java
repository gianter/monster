package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.sungard.hackathon.monster.pojo.FaceImage;
import com.sungard.hackathon.monster.pojo.Person;
import com.sungard.hackathon.monster.service.FaceRegService;
import com.sungard.hackathon.monster.service.FaceTrainService;
import com.sungard.hackathon.monster.service.impl.FaceRegServiceImpl;
import com.sungard.hackathon.monster.service.impl.FaceTrainServiceImpl;

public class FaceTest extends TestCase {

	@Test
	public void testTrain() {
		FaceTrainService train = new FaceTrainServiceImpl();

		List<Person> persons = loadData();

		train.analysisAll(persons);
	}

	@Test
	public void testReg() {
		FaceRegService reg = new FaceRegServiceImpl();

		try {
			String testFile = "test/W3.jpg";
			FileInputStream fis = new FileInputStream(testFile);
			byte[] data = IOUtils.toByteArray(fis);
			
			String who = reg.recogize(data);
			
			System.out.println("current is:" + who);
			
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<Person> loadData() {
		List<Person> persons = new ArrayList<Person>();

		try {
			File resFile = new File("data/images");
			File[] childFiles = resFile.listFiles();
			for (File cf : childFiles) {
				Person person = new Person();
				String name = cf.getName();
				person.setFullName(name);

				System.out.println("child name: " + name);

				File[] images = cf.listFiles();
				for (File img : images) {
					FileInputStream fis = new FileInputStream(img);
					byte[] data = IOUtils.toByteArray(fis);
					fis.close();

					FaceImage faceimg = new FaceImage();
					faceimg.setData(data);
					faceimg.setSuffix("jpg");
					person.getImages().add(faceimg);
				}

				persons.add(person);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return persons;
	}
}
