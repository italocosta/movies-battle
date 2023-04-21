package com.ada.moviesbattle.config;

import com.ada.moviesbattle.model.api.SingleStringResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
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
import java.io.OutputStream;

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
                response.setHeader("WWW-Authenticate","");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                OutputStream responseStream = response.getOutputStream();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(responseStream, new SingleStringResponse().message("Authentication failed"));
                responseStream.flush();
            }
        };
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("user")
            .password("{noop}user")
            .roles("USER")
            .and()
            .withUser("admin")
            .password("{noop}admin")
            .roles("USER");
    }
}