package com.example.thomas.artifact;

public class StudentEntity {
    String key;
    String name;

    // Getters
    public String getKey() {return key;}
    public String getName() {return name;}
    // Setters
    public void setKey(String key) {this.key = key;}
    public void setName(String name) {this.name = name;}
    // Constructor
    StudentEntity(String key, String name) {
        setKey(key);
        setName(name);
    }
    StudentEntity() {
        setName("");
        setKey("");
    }

}
