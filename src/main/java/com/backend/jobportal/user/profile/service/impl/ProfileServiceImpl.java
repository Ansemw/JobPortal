package com.backend.jobportal.user.profile.service.impl;

import com.backend.jobportal.entity.JobPortalUser;
import com.backend.jobportal.entity.Profile;
import com.backend.jobportal.user.profile.dto.ProfileDto;
import com.backend.jobportal.user.profile.repository.ProfilePictureProjection;
import com.backend.jobportal.user.profile.repository.ProfileRepository;
import com.backend.jobportal.user.profile.repository.ProfileResumeProjection;
import com.backend.jobportal.user.profile.service.IProfileService;
import com.backend.jobportal.user.profile.util.ProfileUtil;
import com.backend.jobportal.user.repository.JobPortalUserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileServiceImpl implements IProfileService {

    private final ProfileRepository profileRepository;
    private final JobPortalUserRepository jobPortalUserRepository;

    @Override
    @Transactional
    public ProfileDto createOrUpdateProfile(String email, String profileJson, MultipartFile profilePicture, MultipartFile resume)
    throws JsonProcessingException
    {
        // 1. Resolve the logged-in job seeker.
        JobPortalUser user = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Load the existing profile, or start a new one bound to this user.
        Profile profile = user.getProfile();
        if(profile==null){
            profile = new Profile();
            profile.setUser(user);
        }

        ObjectMapper mapper = new ObjectMapper();
        ProfileDto profileDto = mapper.readValue(profileJson, ProfileDto.class);
        Profile savedProfile = profileRepository.save(ProfileUtil.transformDtoToProfile(profileDto, profile, profilePicture, resume));
        return ProfileUtil.transformProfileToDto(savedProfile, false);
    }

    @Override
    public ProfileDto getProfile(String email) {
        JobPortalUser user = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = user.getProfile();
        if (profile == null) {
            return null;
        }

        return ProfileUtil.transformProfileToDto(profile, false);
    }

    @Override
    public ProfilePictureProjection getProfilePicture(String email) {
        JobPortalUser user = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return profileRepository.findProfilePictureByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile picture not found"));
    }

    @Override
    public ProfileResumeProjection getProfileResume(String email) {
        JobPortalUser user = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return profileRepository.findResumeByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Resume not found"));
    }

}
