package com.backend.jobportal.util;

import com.backend.jobportal.constants.ApplicationConstant;
import com.backend.jobportal.entity.JobPortalUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ApplicationUtility {

    public static String getLoggedInUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()||authentication.getPrincipal() == "anonymousUser"){
            return ApplicationConstant.SYSTEM;
        }

        Object principal = authentication.getPrincipal();
        String userName = null;
        if(principal instanceof JobPortalUser){
            userName = ((JobPortalUser) principal).getEmail();
        }
        else userName = principal.toString();// fallback
        return userName;
    }
}
