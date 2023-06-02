package com.emendes.webflux.config;

import com.emendes.webflux.service.DevDojoUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    http.csrf().disable()
        .authorizeExchange()
        .pathMatchers(HttpMethod.POST, "/animes/**").hasRole("ADMIN")
        .pathMatchers(HttpMethod.GET, "/animes/**").hasRole("USER")
        .anyExchange().authenticated()
        .and().formLogin()
        .and().httpBasic();

    return http.build();
  }

  @Bean
  public ReactiveAuthenticationManager authenticationManager(DevDojoUserDetailsService userDetailsService) {
    return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

}
