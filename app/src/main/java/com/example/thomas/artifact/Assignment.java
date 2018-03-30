package com.example.thomas.artifact;

import java.util.Date;

/**
 * Created by cpye on 3/24/2018.
 */

public class Assignment {
    private String studentName;
    private String assignmentName;
    private Date date;
    private String comments;
    private String fileName;
    private String type;

    // Getters
    public String getStudentName(){return studentName;}
    public Date getDate() {return date;}
    public String getComments() {return comments;}
    public String getAssignmentName() {return assignmentName;}
    public String getFileName() {return fileName;}
    public String getType() {return type;}
    // Setters
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setType(String type) {this.type = type;}
    // Constructor
    Assignment() {
        // set date
        Date d = new Date();
        setDate(d);
    }
}
