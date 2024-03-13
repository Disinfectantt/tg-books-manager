package com.cringe.books.handler;

import com.cringe.books.provider.CustomAuthenticationProvider;
import com.cringe.books.token.CustomAuthenticationToken;
import com.cringe.books.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
    private final UserService userService;

    public CustomAuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        try {
            TreeMap<String, String> params = ((CustomAuthenticationToken) authentication).getParams();
            Map<String, String> userJson = new ObjectMapper().readValue(params.get("user"), new TypeReference<Map<String, String>>() {
            });
            String id = userJson.get("id");
            userService.addUser(Long.parseLong(id));
            String json = mapper.writeValueAsString("Success");
            response.getWriter().write(json);
        } catch (IOException | NumberFormatException e) {
            logger.warn(e.getMessage());
        }
    }

}
