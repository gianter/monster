package com.sungard.hackathon.monster.dao;

import java.util.List;

import com.sungard.hackathon.monster.pojo.Person;

public interface PersonDao {
    
    boolean isExists(String name);
    
    boolean isExists(String name, String email);
    
    void add(Person person);
    
    void addImage2(Person person);
    
    List<Person> findAll();
    
    Person findByName(String name);
    
    Person findByNameAndEmail(String name, String email);
}
