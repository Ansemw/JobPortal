package com.backend.jobportal.user.profile.repository;

public interface ProfileResumeProjection {
    byte[] getResume();
    String getResumeName();
    String getResumeType();
}
