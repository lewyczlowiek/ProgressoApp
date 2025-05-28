package ProgressoApp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSpringSecurity {

  private final UserDetailsService userDetailsService;
  private final JwtService jwtService;

  @Autowired
  public WebSpringSecurity(UserDetailsService userDetailsService, JwtService jwtService) {
    this.userDetailsService = userDetailsService;
    this.jwtService = jwtService;
  }

  @Bean
  public static PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**", "/css/**", "/static/**", "/api/tasks/**")
            .permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/auth/login")
            .permitAll()
            .loginProcessingUrl("/auth/login")
            .defaultSuccessUrl("/", true)
            .failureUrl("/auth/login?error=true"))
        .logout(logout -> logout
            .logoutUrl("/auth/logout")
            .logoutSuccessUrl("/auth/login")
            .permitAll()
        );

    return http.build();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtService, userDetailsService);
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
    return authenticationManagerBuilder.build();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
    builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

}
