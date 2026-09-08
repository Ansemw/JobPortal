package com.backend.jobportal.constants;

public class ApplicationConstant {

    private  ApplicationConstant() {
    throw  new IllegalStateException("Utility class");
    }

    public static final String JWT_SECRET_KEY = "JWT_SECRET";
    public static final String JWT_SECRET_DEFAULT_VALUE = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
    public static final String JWT_HEADER = "Authorization";
    public static final String ROLE_JOB_SEEKER = "ROLE_JOB_SEEKER";
    public static final String ROLE_EMPLOYER = "ROLE_EMPLOYER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String STATUS_SUCCESS = "SUCCESS";


}
