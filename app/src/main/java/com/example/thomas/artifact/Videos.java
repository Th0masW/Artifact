package com.example.thomas.artifact;

import android.graphics.Bitmap;
import android.provider.MediaStore;

public class Videos extends Assignment {
    final static String ASSIGNMENT_TYPE = "VIDEO";
    private MediaStore.Video video;

    // Getters
    public MediaStore.Video getVideo() {return video;}
    // Setters
    public void setVideo(MediaStore.Video video) {
        this.video = video;
    }
    // Constructor
    Videos(String studentName, String assignmentName) {
        setStudentName(studentName);
        setAssignmentName(assignmentName);
        setVideo(video);
        setType(ASSIGNMENT_TYPE);
    }
}
