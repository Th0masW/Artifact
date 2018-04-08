package com.example.thomas.artifact;

import android.provider.MediaStore;

public class Audio extends Assignment {
    final static String ASSIGNMENT_TYPE = "AUDIO";
    private MediaStore.Audio audio;

    public void setAudio(MediaStore.Audio audio) {
        this.audio = audio;
    }
    // Constructor
    Audio(String studentName, String assignmentName) {
        setStudentName(studentName);
        setAssignmentName(assignmentName);
        setType(ASSIGNMENT_TYPE);
    }
}
