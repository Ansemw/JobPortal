package com.backend.jobportal.user.profile.repository;

public interface ProfilePictureProjection {
    byte[] getProfilePicture();
    String getProfilePictureName();
    String getProfilePictureType();
}
