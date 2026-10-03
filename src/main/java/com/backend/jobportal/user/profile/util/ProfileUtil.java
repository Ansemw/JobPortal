package com.backend.jobportal.user.profile.util;

import com.backend.jobportal.entity.Profile;
import com.backend.jobportal.user.profile.dto.ProfileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ProfileUtil {

    private ProfileUtil() {
    }

    // Copies every field of a ProfileDto that has a matching field on Profile onto the given Profile
    // (new or existing) and returns it. userId has no direct counterpart (Profile holds the JobPortalUser),
    // so the caller is responsible for setting profile.user. The profile picture and resume are taken from
    // the uploaded files, and only replaced when a new file was actually sent.
    public static Profile transformDtoToProfile(ProfileDto profileDto, Profile profile, MultipartFile profilePicture, MultipartFile resume) {
        profile.setJobTitle(profileDto.jobTitle());
        profile.setLocation(profileDto.location());
        profile.setExperienceLevel(profileDto.experienceLevel());
        profile.setProfessionalBio(profileDto.professionalBio());
        profile.setPortfolioWebsite(profileDto.portfolioWebsite());
        try {
            if(profilePicture!=null&&!profilePicture.isEmpty()){
                profile.setProfilePicture(profilePicture.getBytes());
                profile.setProfilePictureName(profilePicture.getOriginalFilename());
                profile.setProfilePictureType(profilePicture.getContentType());
            }
        }catch (IOException e){
            throw new RuntimeException("Could not read profile picture");
        }

        try{
            if(resume!=null&&!resume.isEmpty()){
                profile.setResume(resume.getBytes());
                profile.setResumeName(resume.getOriginalFilename());
                profile.setResumeType(resume.getContentType());
            }
        }catch (IOException e){
            throw new RuntimeException("Could not read profile resume");
        }

        profile.setCreatedAt(profileDto.createdAt());
        profile.setUpdatedAt(profileDto.updatedAt());
        return profile;
    }

    // Null-safe on profile. The picture/resume bytes are only included when includeBinaryData is true.
    public static ProfileDto transformProfileToDto(Profile profile, boolean includeBinaryData) {
        if (profile == null) {
            return null;
        }

        return new ProfileDto(
                profile.getId(),
                profile.getUser() != null ? profile.getUser().getId() : null,
                profile.getJobTitle(),
                profile.getLocation(),
                profile.getExperienceLevel(),
                profile.getProfessionalBio(),
                profile.getPortfolioWebsite(),
                includeBinaryData ? profile.getProfilePicture() : null,
                profile.getProfilePictureName(),
                profile.getProfilePictureType(),
                includeBinaryData ? profile.getResume() : null,
                profile.getResumeName(),
                profile.getResumeType(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
