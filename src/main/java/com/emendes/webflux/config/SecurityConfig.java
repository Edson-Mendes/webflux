package com.emendes.webflux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
  public MapReactiveUserDetailsService userDetailsService() {
    PasswordEncoder passwordEncoder = passwordEncoder();

    UserDetails user = User.withUsername("user")
        .password(passwordEncoder.encode("1234567890"))
        .roles("USER")
        .build();

    UserDetails admin = User.withUsername("admin")
        .password(passwordEncoder.encode("1234567890"))
        .roles("USER", "ADMIN")
        .build();

    return new MapReactiveUserDetailsService(user, admin);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

}
