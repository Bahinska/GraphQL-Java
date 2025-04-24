package com.example.lab3.config;


import com.example.lab3.security.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
@Configuration
@EnableWebSecurity
@Slf4j
@Profile("!test") // Exclude this configuration in the "test" profile

public class WebSecurityConfig {

    public static final String ROLE_USER_ADMIN = "ROLE_USER-ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    @Autowired
    private  CustomAccessDeniedHandler accessDeniedHandler;


    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Value("${jwt.auth.converter.principal-attribute:sub}")
    private String principalAttribute;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers( "/favicon.ico", "/graphiql", "/graphiql", "/h2-console/**", "/book").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .accessDeniedHandler(accessDeniedHandler)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAbstractAuthenticationTokenConverter())))
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler) // Custom AccessDeniedHandler
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions
                                .sameOrigin()
                        )
                )
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    private Converter<Jwt, AbstractAuthenticationToken> jwtAbstractAuthenticationTokenConverter() {
        return new Converter<>() {
            private static final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
                    new JwtGrantedAuthoritiesConverter();

            @Override
            public AbstractAuthenticationToken convert(Jwt jwt) {
                Collection<GrantedAuthority> authorities =
                        Stream.concat(
                                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                                extractRoles(jwt).stream()
                        ).collect(Collectors.toSet());
                return new JwtAuthenticationToken(jwt, authorities, jwt.getClaim(principalAttribute));
            }
        };
    }

    private Collection<? extends GrantedAuthority> extractRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        Map<String, Object> resource;
        Collection<String> resourceRoles;
        log.info("Resource access: {}", resourceAccess);
        if (resourceAccess == null
                || (resource = (Map<String, Object>) resourceAccess.get(resourceId)) == null
                || (resourceRoles = (Collection<String>) resource.get("roles")) == null) {
            return Collections.emptySet();
        }
        log.info("Resource roles: {}", resourceRoles);
        log.info("Resource: {}", resource);
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}