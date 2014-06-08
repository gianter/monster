package test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sungard.hackathon.monster.dao.PersonDao;
import com.sungard.hackathon.monster.pojo.FaceImage;
import com.sungard.hackathon.monster.pojo.Person;

public class DaoTest {
    protected Connection conn;
    
    public DaoTest() {
    }
    
    @Test
    public void testAdd() throws SQLException {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        PersonDao personDao = (PersonDao) ctx.getBean("PersonDao");
        
        Person person = createPerson();
        personDao.add(person);
        
        List<Person> list1 = personDao.findAll();
        Assert.assertEquals(1, list1.size());
        
        List<Person> list2 = personDao.findByName("test");
        Assert.assertEquals(1, list2.size());
    }
    
    private Person createPerson() {
        Person person = new Person();
        person.setFullName("test");
        person.setPhoneNumber("111111");
        
        FaceImage image1 = new FaceImage();
        image1.setSuffix("jpg");
        person.setImage1(image1);
        
        FaceImage image2 = new FaceImage();
        image2.setSuffix("png");
        person.setImage1(image2);
        
        FaceImage image3 = new FaceImage();
        image3.setSuffix("gif");
        person.setImage1(image3);
        
        return person;
    }
}
