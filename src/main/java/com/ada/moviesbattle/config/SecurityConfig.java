package com.ada.moviesbattle.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] URI_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/h2/**",
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable().authorizeRequests()
                .antMatchers(URI_WHITELIST).permitAll()
                .anyRequest().authenticated()
            .and().headers().frameOptions().disable()
            .and().httpBasic()
            .and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            @ExceptionHandler(value = {AuthenticationException.class})
            public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) throws
                    IOException, ServletException {
                // 401
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
            }

            @SuppressWarnings("UnusedParameters")
            @ExceptionHandler(value = {AccessDeniedException.class})
            public void commence(final HttpServletRequest request, final HttpServletResponse response, final AccessDeniedException accessDeniedException) throws IOException {
                // 403
                response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
            }

            @SuppressWarnings("UnusedParameters")
            @ExceptionHandler(value = {Exception.class})
            public void commence(final HttpServletRequest request, final HttpServletResponse response, final Exception exception) throws IOException {
                // 500
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
            }
        };
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("italo")
            .password("{noop}italo")
            .roles("USER");
    }
}