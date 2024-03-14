package com.cringe.books.provider;

import com.cringe.books.service.UserService;
import com.cringe.books.token.CustomAuthenticationToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
    private final UserService userService;
    @Value("${botToken}")
    private String botToken;

    public CustomAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        TreeMap<String, String> params = ((CustomAuthenticationToken) authentication).getParams();
        String hash = params.get("hash");
        if (!params.containsKey("auth_date") || !params.containsKey("user") || hash == null) {
            throw new BadCredentialsException("Hash, user, or auth_date is null");
        }
        params.remove("hash");
        if (!checkTgHash(hash, params)) {
            throw new BadCredentialsException("Hash does not match");
        }
        TreeMap<String, String> userJson;
        try {
            userJson = new ObjectMapper().readValue(params.get("user"), new TypeReference<TreeMap<String, String>>() {
            });
        } catch (IOException | NumberFormatException e) {
            logger.error(e.getMessage());
            throw new BadCredentialsException("User field invalid");
        }
        Long id = Long.parseLong(userJson.get("id"));
        if (userService.isWhitelist() && !userService.isInWhitelist(id)) {
            throw new BadCredentialsException("You are not in whitelist");
        }
        return new CustomAuthenticationToken(userJson, true);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CustomAuthenticationToken.class);
    }

    protected boolean checkTgHash(String hash, TreeMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            if (iterator.hasNext()) {
                sb.append("\n");
            }
        }
        byte[] secretKey = new HmacUtils("HmacSHA256", "WebAppData").hmac(botToken);
        String hashCalculated = new HmacUtils("HmacSHA256", secretKey).hmacHex(sb.toString());
        return hash.equals(hashCalculated);
    }

}
