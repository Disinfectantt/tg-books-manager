package com.cringe.books.handler;

import com.cringe.books.JwtMain;
import com.cringe.books.provider.CustomAuthenticationProvider;
import com.cringe.books.service.UserService;
import com.cringe.books.token.CustomAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.TreeMap;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
    private final UserService userService;
    private final JwtMain jwtMain;
    @Value("${tokenLifetime}")
    private long lifetime;

    public CustomAuthenticationSuccessHandler(UserService userService, JwtMain jwtMain) {
        this.userService = userService;
        this.jwtMain = jwtMain;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            TreeMap<String, String> params = ((CustomAuthenticationToken) authentication).getParams();
            String id = params.get("id");
            userService.addUser(Long.parseLong(id));
            response.setContentType("application/json;charset=UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            String json = mapper.writeValueAsString("Success");
            setJwtCookie(request, response, id);
            response.getWriter().write(json);
        } catch (IOException | NumberFormatException e) {
            logger.warn(e.getMessage());
        }
    }

    protected void setJwtCookie(HttpServletRequest request, HttpServletResponse response, String id) {
        String jwt = jwtMain.createJwt(id);
        Cookie cookie = new Cookie(jwtMain.getCookieName(), jwt);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
//        cookie.setDomain(request.getServerName());
//        cookie.setMaxAge((int)lifetime);
        //TODO
        response.addCookie(cookie);
    }

}
