
package com.snd.server.config;

import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import com.snd.server.constant.RoleConstant;
import com.snd.server.constant.SecurityConstant;
import com.snd.server.security.CustomAccessDeniedHandler;
import com.snd.server.security.CustomAuthenticationEntryPoint;
import com.snd.server.security.JwtBlacklistFilter;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtKey;

    @SuppressWarnings("deprecation")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            JwtBlacklistFilter jwtBlacklistFilter) throws Exception {
        httpSecurity
                .csrf(c -> c.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityConstant.ADMIN_URLS).hasAuthority(RoleConstant.ROLE_ADMIN)

                        .requestMatchers(SecurityConstant.QUAN_LY_CUA_HANG_URLS)
                        .hasAuthority(RoleConstant.ROLE_QUAN_LY_CUA_HANG)

                        .requestMatchers(SecurityConstant.NHAN_VIEN_BAN_HANG_URLS)
                        .hasAnyAuthority(RoleConstant.ROLE_NHAN_VIEN_BAN_HANG, RoleConstant.ROLE_QUAN_LY_CUA_HANG)

                        .requestMatchers(SecurityConstant.CHAM_SOC_KHACH_HANG_URLS)
                        .hasAnyAuthority(RoleConstant.ROLE_CHAM_SOC_KHACH_HANG, RoleConstant.ROLE_QUAN_LY_CUA_HANG)

                        .requestMatchers(SecurityConstant.KHACH_HANG_URLS)
                        .hasAnyAuthority(RoleConstant.ROLE_NHAN_VIEN_KHO, RoleConstant.ROLE_QUAN_LY_CUA_HANG)

                        .requestMatchers(SecurityConstant.KHACH_HANG_URLS).hasAuthority(RoleConstant.ROLE_KHACH_HANG)

                        .requestMatchers(SecurityConstant.PUBLIC_URLS).permitAll()

                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(jwtBlacklistFilter,
                        BearerTokenAuthenticationFilter.class)
                .formLogin(f -> f.disable())

                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(SecurityConstant.JWT_ALGORITHM)
                .build();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length,
                SecurityConstant.JWT_ALGORITHM.getName());
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            if (role == null) {
                return List.of();
            }
            String grantedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            return List.of(new SimpleGrantedAuthority(grantedRole));
        });

        return converter;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return new DefaultBearerTokenResolver();
    }

}
