package com.backend.jobportal.security;

import com.backend.jobportal.entity.JobPortalUser;
import com.backend.jobportal.user.repository.JobPortalUserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class UserPassAuthenticationProvider implements AuthenticationProvider {

    private final JobPortalUserRepository jobPortalUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        JobPortalUser user = jobPortalUserRepository.findByEmail(username)
                            .orElseThrow(()-> new UsernameNotFoundException("User details not found for username:"+username));

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName()));

        if(passwordEncoder.matches(password, user.getPasswordHash()))
            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        else
         throw new BadCredentialsException("Invalid password");
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));

    }
}
