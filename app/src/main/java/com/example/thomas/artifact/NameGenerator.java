package com.example.thomas.artifact;

import android.util.Log;

import java.util.Random;

public class NameGenerator {
    private String fileName;
    private String studentName;
    private String fileExtension;
    final static String TAG = "NameGenerator";
    // getters
    public String getFileName() {return fileName;}
    public String getStudentName() {return studentName;}
    // setters
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    public String createNewFileName(String studentName, String fileExtension) {
        randomFileName(this.studentName, this.fileExtension);
        return getFileName();
    }
    // Constructors
    NameGenerator(String studentName, String fileExtension) {
        setStudentName(studentName);
        this.fileExtension = fileExtension;
        randomFileName(this.studentName, this.fileExtension);
    }

    private void randomFileName(String name, String fileExtension) {
        String shortName = name;
        shortName.trim();
        shortName = shortName.replaceAll(" ", "_");
        Log.v(TAG,"Short name:"+ shortName);
        Integer num = getRandomNumberInRange(1000,9999);
        fileName = shortName + num.toString() + "." + fileExtension;
        Log.v(TAG, fileName);
    }

    private static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
