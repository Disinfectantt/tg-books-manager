package com.cringe.books.filter;

import com.cringe.books.JwtMain;
import com.cringe.books.provider.CustomAuthenticationProvider;
import com.cringe.books.token.CustomAuthenticationToken;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.TreeMap;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    private final JwtMain jwtMain;

    public JwtAuthenticationFilter(JwtMain jwtMain) {
        this.jwtMain = jwtMain;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = getJwtFromCookie(request);
        String id;
        if (jwt == null) {
            response.sendRedirect("/login");
            return;
        }
        try {
            id = jwtMain.verifyToken(jwt);
        } catch (JwtException | IllegalArgumentException e) {
            response.sendRedirect("/login");
            return;
        }
        if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            TreeMap<String, String> params = new TreeMap<>();
            params.put("id", id);
            CustomAuthenticationToken auth = new CustomAuthenticationToken(params, true);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/login")
                || path.contains(".js")
                || path.contains(".css");
    }

    protected String getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtMain.getCookieName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
