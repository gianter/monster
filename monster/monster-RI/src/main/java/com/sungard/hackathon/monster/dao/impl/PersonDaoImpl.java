package com.sungard.hackathon.monster.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sungard.hackathon.monster.dao.PersonDao;
import com.sungard.hackathon.monster.pojo.FaceImage;
import com.sungard.hackathon.monster.pojo.Person;

@Component("PersonDao")
public class PersonDaoImpl implements PersonDao {
    @Autowired
    private DataSource dataSource;
    
    @Override
    public void add(Person person) {
        String sql = "insert into person(FULLNAME,PHONENUMBER,IMAGE1,IMAGE2,IMAGE3) values(?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, person.getFullName());
            ps.setString(2, person.getPhoneNumber());
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
    }
    
    @Override
    public List<Person> findAll() {
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
                person.setFullName(rs.getString("FULLNAME"));
                person.setPhoneNumber(rs.getString("PHONENUMBER"));
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
        
        return results;
    }
    
    @Override
    public List<Person> findByName(String name) {
        String sql = "select * from person where FULLNAME=?";
        Connection conn = null;
        PreparedStatement ps = null;
        List<Person> results = new ArrayList<Person>();
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Person person = new Person();
                person.setFullName(rs.getString("FULLNAME"));
                person.setPhoneNumber(rs.getString("PHONENUMBER"));
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
        
        return results;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
}
