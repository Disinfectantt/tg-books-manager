package com.cringe.books;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.TreeMap;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {
    private TreeMap<String, String> params;

    public CustomAuthenticationToken(TreeMap<String, String> params, boolean isAuth) {
        super(null);
        this.params = params;
        setAuthenticated(isAuth);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    public TreeMap<String, String> getParams() {
        return params;
    }

    public void setParams(TreeMap<String, String> params) {
        this.params = params;
    }

}
