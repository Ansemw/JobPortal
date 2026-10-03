package com.backend.jobportal.user.profile.repository;

import com.backend.jobportal.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

    @Query("select p.profilePicture as profilePicture, p.profilePictureName as profilePictureName, " +
            "p.profilePictureType as profilePictureType from Profile p where p.user.id = :userId")
    Optional<ProfilePictureProjection> findProfilePictureByUserId(Long userId);

    @Query("select p.resume as resume, p.resumeName as resumeName, " +
            "p.resumeType as resumeType from Profile p where p.user.id = :userId")
    Optional<ProfileResumeProjection> findResumeByUserId(Long userId);
}
