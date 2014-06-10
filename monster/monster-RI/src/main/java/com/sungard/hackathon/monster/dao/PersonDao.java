package com.sungard.hackathon.monster.dao;

import java.util.List;

import com.sungard.hackathon.monster.pojo.Person;

public interface PersonDao {
    void add(Person person);
    
    void addImage2(Person person);
    
    List<Person> findAll();
    
    List<Person> findByName(String name);
    
    List<Person> findByNameAndEmail(String name,String email);
}
