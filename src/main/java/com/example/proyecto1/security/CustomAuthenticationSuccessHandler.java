package com.example.proyecto1.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectUrl = "/";

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            switch (auth.getAuthority()) {
                case "ROLE_ADMIN":
                    redirectUrl = "/admin/dashboard";
                    break;
                case "ROLE_EMPRESA":
                    redirectUrl = "/empresa/dashboard";
                    break;
                case "ROLE_OFERENTE":
                    redirectUrl = "/oferente/dashboard";
                    break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}