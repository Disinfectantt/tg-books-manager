package com.cringe.books;

import com.cringe.books.filter.CustomAuthenticationFilter;
import com.cringe.books.filter.JwtAuthenticationFilter;
import com.cringe.books.handler.CustomAuthenticationFailureHandler;
import com.cringe.books.provider.CustomAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final CustomAuthenticationFailureHandler failureHandler;
    private final CustomAuthenticationProvider authProvider;
    private final CustomAuthenticationFilter customAuthenticationFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(CustomAuthenticationProvider authProvider,
                                 CustomAuthenticationFailureHandler failureHandler,
                                 CustomAuthenticationFilter customAuthenticationFilter,
                                 JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.authProvider = authProvider;
        this.failureHandler = failureHandler;
        this.customAuthenticationFilter = customAuthenticationFilter;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authProvider));
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authenticationProvider(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/*.js", "/*.css").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureHandler(failureHandler)
                        .permitAll())
                .addFilterBefore(customAuthenticationFilter,
                        BasicAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter,
                        BasicAuthenticationFilter.class)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

}
