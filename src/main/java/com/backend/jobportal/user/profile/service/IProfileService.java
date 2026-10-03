package com.backend.jobportal.user.profile.service;

import com.backend.jobportal.user.profile.dto.ProfileDto;
import com.backend.jobportal.user.profile.repository.ProfilePictureProjection;
import com.backend.jobportal.user.profile.repository.ProfileResumeProjection;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

public interface IProfileService {

    // Creates the logged-in job seeker's profile, or updates it if one already exists, returning the saved profile.
    public ProfileDto createOrUpdateProfile(String email, String profileJson, MultipartFile profilePicture, MultipartFile resume)
    throws JsonProcessingException;

    // Fetches the logged-in job seeker's profile by email, excluding binary data (profile picture/resume bytes).
    public ProfileDto getProfile(String email);

    // Fetches only the profile picture bytes/name/type for the logged-in job seeker, without loading the rest of the profile.
    public ProfilePictureProjection getProfilePicture(String email);

    // Fetches only the resume bytes/name/type for the logged-in job seeker, without loading the rest of the profile.
    public ProfileResumeProjection getProfileResume(String email);
}
