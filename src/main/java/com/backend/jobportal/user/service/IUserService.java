package com.backend.jobportal.user.service;

import com.backend.jobportal.user.dto.UserDto;

import java.util.Optional;

public interface IUserService {

    // Looks up a user by email and returns it as a DTO, if one exists.
    Optional<UserDto> findUserByEmail(String email);

    // Updates the role of the user identified by userId to ROLE_EMPLOYER.
    // Returns the updated user as a DTO, if a user exists with that id.
    Optional<UserDto> elevateUserToEmployer(Long userId);

    // Associates the user identified by userId (who must already hold ROLE_EMPLOYER) with
    // the company identified by companyId. Returns the updated user as a DTO, if the user exists.
    Optional<UserDto> assignCompanyToUser(Long userId, Long companyId);
}
