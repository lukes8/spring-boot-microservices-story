package com.lukestories.microservices.user_ws.web.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(2) // Second in order
public class CustomAuthenticationFilter extends HttpFilter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("AuthenticationFilter: Checking authentication for " + request.getRequestURL());
        // Simulate authentication logic
        boolean isAuthenticated = true; // This would normally involve checking tokens or session
        if (isAuthenticated) {
            chain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

}