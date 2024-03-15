package com.cringe.books.filter;

import com.cringe.books.handler.CustomAuthenticationFailureHandler;
import com.cringe.books.handler.CustomAuthenticationSuccessHandler;
import com.cringe.books.provider.CustomAuthenticationProvider;
import com.cringe.books.token.CustomAuthenticationToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

@Component
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    @Lazy
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager,
                                      CustomAuthenticationFailureHandler customAuthenticationFailureHandler,
                                      CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        super("/loginTelegram", authenticationManager);
        setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST") || !request.getHeader("Content-Type").equals("application/json")) {
            throw new AuthenticationServiceException("Authentication method not supported");
        }
        TreeMap<String, String> params;
        try {
            params = getBody(request);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Invalid json");
        }
        String str = java.net.URLDecoder.decode(params.get("initData"), StandardCharsets.UTF_8);
        String[] elements = str.split("&");
        params.clear();
        for (String element : elements) {
            String[] tmp = element.split("=");
            if (tmp.length > 1) {
                params.put(tmp[0], tmp[1]);
            } else {
                params.put(tmp[0], "");
            }
        }
        CustomAuthenticationToken authRequest = new CustomAuthenticationToken(params, false);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected TreeMap<String, String> getBody(HttpServletRequest request) throws AuthenticationException, IOException {
        try (BufferedReader reader = request.getReader()) {
            String jsonBody = getJsonString(reader);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonBody, new TypeReference<TreeMap<String, String>>() {
            });
        }
    }

    protected String getJsonString(BufferedReader reader) throws IOException {
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }

}
