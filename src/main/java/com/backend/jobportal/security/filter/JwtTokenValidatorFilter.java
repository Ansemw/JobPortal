package com.backend.jobportal.security.filter;

import com.backend.jobportal.constants.ApplicationConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

private final AntPathMatcher matcher = new AntPathMatcher();

@Qualifier("publicPaths")
private final List<String> publicPaths;

@Qualifier("regexPaths")
private final List<String> regexPaths;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean toBeFilteredPublic, toBeFilteredRegex;
        String path = request.getRequestURI();

        //List<String> combined = Stream.concat(publicPaths.stream(), regexPaths.stream()).toList();

        toBeFilteredPublic = publicPaths.stream().anyMatch(p->matcher.match(path,p));
        toBeFilteredRegex = regexPaths.stream().anyMatch(path::matches);
        return toBeFilteredPublic || toBeFilteredRegex;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(ApplicationConstant.JWT_HEADER);

        if(authHeader !=null && !authHeader.isEmpty()){
            try{
                String jwt =  authHeader.substring(7);
                Environment env = getEnvironment();

                    String secret = env.getProperty(ApplicationConstant.JWT_SECRET_KEY
                            , ApplicationConstant.JWT_SECRET_DEFAULT_VALUE);

                    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));


                        Claims claims = Jwts.parser().verifyWith(secretKey)
                                .build()
                                .parseSignedClaims(jwt).getPayload();

                        String username = String.valueOf(claims.get("email"));
                        String roles = String.valueOf(claims.get("roles"));
                        Authentication authentication = new UsernamePasswordAuthenticationToken(username,
                                null, AuthorityUtils.commaSeparatedStringToAuthorityList(roles));
                        SecurityContextHolder.getContext().setAuthentication(authentication);


            }catch(ExpiredJwtException exception){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT token");
                return;

            }catch(Exception exception){
                throw new BadCredentialsException("Invalid JWT token");
            }

        }

        filterChain.doFilter(request, response);

    }


}
