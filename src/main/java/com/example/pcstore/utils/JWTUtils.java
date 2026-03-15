package com.example.pcstore.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.pcstore.exception.AuthenticationException;
import com.example.pcstore.model.response.RolesUserResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JWTUtils {
    private JWTUtils() {
    }

    private static final String BEARER = "Bearer ";
    private static final String REALM_ACCESS = "realm_access";
    private static final String AUTHORIZATION = "Authorization";
    private static final String PREFERRED_USERNAME = "preferred_username";

    public static String getUsername() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String authorization = request.getHeader(AUTHORIZATION);
            String token = StringUtils.isNotNullOrEmpty(authorization) ? authorization.replace(BEARER, StringUtils.EMPTY) : StringUtils.EMPTY;
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim(PREFERRED_USERNAME).asString();
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    public static List<String> getRoles(String tokenCurrent) {
        try {
            String token = tokenCurrent.startsWith(BEARER) ? tokenCurrent.replace(BEARER, StringUtils.EMPTY) : tokenCurrent;
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim(REALM_ACCESS).as(RolesUserResponse.class).getRoles().stream().filter(role -> role.startsWith("ROLE")).toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static String getCurrentToken() {
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (servletRequestAttributes != null) {
                HttpServletRequest request = servletRequestAttributes.getRequest();
                String authorization = request.getHeader(AUTHORIZATION);
                return StringUtils.isNotNullOrEmpty(authorization) ? authorization.replace(BEARER, StringUtils.EMPTY) : StringUtils.EMPTY;
            }
        } catch (Exception e) {

            throw new AuthenticationException();
        }
        return StringUtils.EMPTY;
    }

    public static Date getExpiredTime(String tokenCurrent) {
        try {
            String token = tokenCurrent.startsWith(BEARER) ? tokenCurrent.replace(BEARER, StringUtils.EMPTY) : tokenCurrent;
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("exp").asDate();
        } catch (Exception e) {
            return new Date();
        }
    }
}
