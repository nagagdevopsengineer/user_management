package com.arrivnow.usermanagement.usermanagement.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";
    
    public static final String EMPLOYEE = "ROLE_EMPLOYEE";
    
    public static final String CLIENT = "ROLE_CLIENT";
    
    public static final String CONTRACTOR = "ROLE_CONTRACTOR";
    
    public static final String DRIVER = "ROLE_DRIVER";
    
    public static final String HELPER = "ROLE_HELPER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {}
}
