package org.sopt.makers.api.common.config;

import static org.sopt.makers.api.common.security.SecurityConstant.ADMIN;
import static org.sopt.makers.api.common.security.SecurityConstant.INTERNAL_SERVICE;
import static org.sopt.makers.api.common.security.SecurityConstant.PATTERN_ALL;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.security.filter.ApiKeyAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.sopt.makers.api.common.security.filter.AuthenticationExceptionFilter;
import org.sopt.makers.api.common.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AuthenticationExceptionFilter authenticationExceptionFilter;
  private final SecurityProperty securityProperty;

  @Bean
  public static PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  @Profile({"local", "test"})
  public SecurityFilterChain filterChainLocalAndTest(final HttpSecurity http) throws Exception {
    setDefaultHttp(http);
    http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }

  @Bean
  @Profile("dev")
  public SecurityFilterChain filterChainDev(final HttpSecurity http) throws Exception {
    setDefaultHttp(http);
    setSecuredHttp(http, true);
    return http.build();
  }

  @Bean
  @Profile("prod")
  public SecurityFilterChain filterChainProd(final HttpSecurity http) throws Exception {
    setDefaultHttp(http);
    setSecuredHttp(http, false);
    return http.build();
  }

  private void setDefaultHttp(final HttpSecurity http) throws Exception {
    http.httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(apiKeyAuthenticationFilter, JwtAuthenticationFilter.class)
        .addFilterBefore(authenticationExceptionFilter, ApiKeyAuthenticationFilter.class);
  }

  private void setSecuredHttp(final HttpSecurity http, final boolean includeSwagger)
      throws Exception {
    List<String> securedEndpoints = securityProperty.api().securedEndpoints();
    http.authorizeHttpRequests(
        authorize -> {
          authorize
              .requestMatchers("/api/v1/auth/**")
              .permitAll()
              .requestMatchers("/api/v1/social/accounts/**")
              .permitAll()
              .requestMatchers("/api/v1/official/**")
              .permitAll()
              .requestMatchers("/api/v1/admin/auth/login", "/api/v1/admin/auth/refresh")
              .permitAll()
              .requestMatchers("/api/v1/admin/**")
              .hasAuthority(ADMIN)
              .requestMatchers("/error/**")
              .permitAll()
              .requestMatchers(HttpMethod.POST, "/api/v1/notification/register")
              .permitAll()
              .requestMatchers("/api/v1/visitor/**")
              .permitAll();

          if (includeSwagger) {
            authorize
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                .permitAll();
          }
          for (String endpoint : securedEndpoints) {
            authorize.requestMatchers(endpoint + PATTERN_ALL).hasRole(INTERNAL_SERVICE);
          }

          authorize.anyRequest().authenticated();
        });
  }
}
