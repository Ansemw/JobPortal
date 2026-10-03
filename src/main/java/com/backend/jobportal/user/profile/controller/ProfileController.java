package com.backend.jobportal.user.profile.controller;

import com.backend.jobportal.user.profile.dto.ProfileDto;
import com.backend.jobportal.user.profile.repository.ProfilePictureProjection;
import com.backend.jobportal.user.profile.repository.ProfileResumeProjection;
import com.backend.jobportal.user.profile.service.IProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final IProfileService profileService;

    @PutMapping(value = "/jobseeker", version = "1.0", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileDto> createOrUpdateProfile(
            @RequestPart(value = "profile") String profileJson,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            Authentication authentication
            ) throws JsonProcessingException {

        String email = authentication.getName();
        ProfileDto savedProfile = profileService.createOrUpdateProfile(email, profileJson, profilePicture, resume);
        return ResponseEntity.ok().body(savedProfile);

    }

    @GetMapping(value = "/jobseeker", version = "1.0")
    public ResponseEntity<ProfileDto> getProfile(Authentication authentication) {
        String email = authentication.getName();
        ProfileDto profileDto = profileService.getProfile(email);
        return ResponseEntity.ok().body(profileDto);
    }

    @GetMapping(value = "/picture/jobseeker", version = "1.0")
    public ResponseEntity<byte[]> getProfilePicture(Authentication authentication) {
        String email = authentication.getName();
        ProfilePictureProjection profilePicture = profileService.getProfilePicture(email);
        byte[] profilePictureData = profilePicture.getProfilePicture();
        if (profilePictureData == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(profilePicture.getProfilePictureType()));
        headers.setContentLength(profilePictureData.length);
        return new ResponseEntity<>(profilePictureData, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/resume/jobseeker", version = "1.0")
    public ResponseEntity<byte[]> getProfileResume(Authentication authentication) {
        String email = authentication.getName();
        ProfileResumeProjection resume = profileService.getProfileResume(email);
        byte[] resumeData = resume.getResume();
        if (resumeData == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(resume.getResumeType()));
        headers.setContentLength(resumeData.length);
        headers.setContentDispositionFormData("attachment", resume.getResumeName());
        return new ResponseEntity<>(resumeData, headers, HttpStatus.OK);
    }
}
