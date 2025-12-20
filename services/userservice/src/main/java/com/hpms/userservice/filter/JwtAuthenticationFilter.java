package com.hpms.userservice.filter;

import com.hpms.userservice.config.SecurityUserDetailsService;
import com.hpms.userservice.utils.JwtTokenUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtTokenUtils jwtTokenUtils;
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenUtils jwtTokenUtils, SecurityUserDetailsService userDetailsService) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, JwtException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            jwt = authHeader.substring(7);

            userEmail = jwtTokenUtils.extractUsername(jwt);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(userEmail);
                if (jwtTokenUtils.validateToken(jwt, user)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),
                            null, user.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            response.setStatus(401);
            response.setContentType("application/json");
            JSONObject resObject = new JSONObject();
            resObject.appendField("error", "Token is expired or you have no authority to do this function");
            resObject.appendField("ok", false);
            resObject.appendField("status", 401);
            response.getWriter().write(resObject.toJSONString());
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().endsWith("/login") ||
                request.getServletPath().endsWith("/register") ||
                request.getServletPath().endsWith("/confirm-email") ||
                request.getServletPath().endsWith("/activate-email") ||
                request.getServletPath().endsWith("/refresh-token");
    }
}

