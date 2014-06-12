package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.sungard.hackathon.monster.utils.FloatUtil;

public class FaceTest extends TestCase {

	@Test
	public void testTrain() {

		FaceTrainService train = new FaceTrainServiceImpl();

		List<Person> persons = loadData();

		train.analysisAll(persons);
	}

	@Test
	public void test1() {
		double d1 = 1.695198E9;
		double d2 = 12;
		double re = Math.sqrt(d1 / d2);
		System.out.println(d1 / d2);
		System.out.println(re);
		System.out.println(Math.sqrt(1.412665E8));
		System.out.println(Math.sqrt(1.41266));
		System.out.println(Math.sqrt(1.41));
		System.out.println(Math.sqrt(9));

		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		BigDecimal b3 = b1.divide(b2, 5, RoundingMode.CEILING);
		b3.setScale(4);
		System.out.println("b3 is: " + b3);
		System.out.println(Math.sqrt(b3.doubleValue()));
		System.out.println(sqrt(b3, 4).doubleValue());
		System.out.println(FloatUtil.sqrt(b3, RoundingMode.HALF_EVEN));
	}

	public BigDecimal sqrt(BigDecimal in, final int scale) {
		BigDecimal sqrt = new BigDecimal(1);
		sqrt.setScale(scale + 3, RoundingMode.FLOOR);
		BigDecimal store = new BigDecimal(in.toString());
		boolean first = true;
		do {
			if (!first) {
				store = new BigDecimal(sqrt.toString());
			} else
				first = false;
			store.setScale(scale + 3, RoundingMode.FLOOR);
			sqrt = in
					.divide(store, scale + 3, RoundingMode.FLOOR)
					.add(store)
					.divide(BigDecimal.valueOf(2), scale + 3,
							RoundingMode.FLOOR);
		} while (!store.equals(sqrt));
		return sqrt.setScale(scale, RoundingMode.FLOOR);
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
			e.printStackTrace();
		} catch (IOException e) {
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
				person.setName(name);

				System.out.println("child name: " + name);

				File[] images = cf.listFiles();
				for (int i = 0; i < images.length; i++) {
					FileInputStream fis = new FileInputStream(images[i]);
					byte[] data = IOUtils.toByteArray(fis);
					fis.close();

					FaceImage faceimg = new FaceImage();
					faceimg.setData(data);
					faceimg.setSuffix("jpg");

					if (i == 0) {
						person.setImage1(faceimg);
					} else {
						person.setImage2(faceimg);
					}
				}

				persons.add(person);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return persons;
	}
}
