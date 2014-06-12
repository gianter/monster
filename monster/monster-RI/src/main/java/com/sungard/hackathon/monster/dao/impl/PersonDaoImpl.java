package com.sungard.hackathon.monster.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sungard.hackathon.monster.dao.PersonDao;
import com.sungard.hackathon.monster.pojo.FaceImage;
import com.sungard.hackathon.monster.pojo.Person;

@Component("PersonDao")
public class PersonDaoImpl implements PersonDao {
    Logger logger = Logger.getLogger(this.getClass());
    @Autowired
    private DataSource dataSource;
    
    @Override
    public boolean isExists(String name) {
        return isExists(name, null);
    }
    
    public boolean isExists(String name, String email) {
        logger.info("start isExist method");
        logger.info("name:"+name);
        logger.info("email"+email);
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from person where NAME=? ");
        if (StringUtils.isNotBlank(email)) {
            sb.append(" and EMAIL=? ");
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        int count = 0;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sb.toString());
            ps.setString(1, name);
            
            if (StringUtils.isNotBlank(email)) {
                ps.setString(2, email);
            }
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("end isExist method");
        return count > 0;
    }
    
    @Override
    public void add(Person person) {
        logger.info("start add method");
        logger.info("name:"+person.getName());
        logger.info("email"+person.getEmail());
        String sql = "insert into person(NAME,EMAIL,IMAGE1,IMAGE2,IMAGE3) values(?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, person.getName());
            ps.setString(2, person.getEmail());
            ps.setObject(3, person.getImage1());
            ps.setObject(4, person.getImage2());
            ps.setObject(5, person.getImage3());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("end add method");
    }
    
    @Override
    public void addImage2(Person person) {
        logger.info("start addImage2 method");
        logger.info("name:"+person.getName());
        logger.info("email"+person.getEmail());
        String sql = "update person set IMAGE2=? where name=? and email=? ";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, person.getImage2());
            ps.setString(2, person.getName());
            ps.setString(3, person.getEmail());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("end addImage2 method");
    }
    
    @Override
    public List<Person> findAll() {
        logger.info("start find all method");
        String sql = "select * from person";
        Connection conn = null;
        Statement st = null;
        List<Person> results = new ArrayList<Person>();
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                Person person = new Person();
                person.setName(rs.getString("NAME"));
                person.setEmail(rs.getString("EMAIL"));
                person.setImage1((FaceImage) rs.getObject("IMAGE1"));
                person.setImage2((FaceImage) rs.getObject("IMAGE2"));
                person.setImage3((FaceImage) rs.getObject("IMAGE3"));
                results.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("end find all method");
        return results;
    }
    
    @Override
    public Person findByName(String name) {
        logger.info("start findByName method");
        logger.info("name:"+name);
        String sql = "select * from person where NAME=?";
        Connection conn = null;
        PreparedStatement ps = null;
        Person person = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                person = new Person();
                person.setName(rs.getString("NAME"));
                person.setEmail(rs.getString("EMAIL"));
                person.setImage1((FaceImage) rs.getObject("IMAGE1"));
                person.setImage2((FaceImage) rs.getObject("IMAGE2"));
                person.setImage3((FaceImage) rs.getObject("IMAGE3"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("end findByName method");
        
        return person;
    }
    
    @Override
    public Person findByNameAndEmail(String name,String email) {
        logger.info("start findByNameAndEmail method");
        logger.info("name:"+name);
        logger.info("email"+email);
        String sql = "select * from person where NAME=? and email=? ";
        Connection conn = null;
        PreparedStatement ps = null;
        Person person = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                person = new Person();
                person.setName(rs.getString("NAME"));
                person.setEmail(rs.getString("EMAIL"));
                person.setImage1((FaceImage) rs.getObject("IMAGE1"));
                person.setImage2((FaceImage) rs.getObject("IMAGE2"));
                person.setImage3((FaceImage) rs.getObject("IMAGE3"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("end findByNameAndEmail method");
        return person;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
