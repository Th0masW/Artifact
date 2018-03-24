package com.example.thomas.artifact;

import android.graphics.Bitmap;

/**
 * Created by cpye on 3/24/2018.
 */

public class Pictures extends Assignment {
    private Bitmap photo;

    // Getters
    public Bitmap getPhoto() {return photo;}
    // Setters
    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
    // Constructor
    Pictures(String studentName, String assignmentName, Bitmap picture) {
        setStudentName(studentName);
        setAssignmentName(assignmentName);
        setPhoto(picture);
    }
    Pictures() {}
}
