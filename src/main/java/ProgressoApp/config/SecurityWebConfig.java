package ProgressoApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityWebConfig {

  private final JwtAuthFilter jwtAuthFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable) // Disable CSRF (since we're using stateless JWT)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/register", "/auth/login", "/auth/register/save", "/", "/privacy-policy.html")
            .permitAll()
            .requestMatchers("/admin/*", "/admin").hasAnyRole("ADMIN")
            .anyRequest().authenticated())
        .exceptionHandling(ex -> ex
            .accessDeniedHandler(accessDeniedHandler()) // ⬅️ Dodajemy handler
        )
        .formLogin(login -> login
            .loginPage("/login")
            .permitAll()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .logout(logout -> logout
            .logoutUrl("/auth/logout")  // dopasuj do twojego endpointa
            .logoutSuccessUrl("/auth/login?logout")
            .deleteCookies("jwtToken")
            .permitAll()
        )
        .addFilterBefore(jwtAuthFilter,
            UsernamePasswordAuthenticationFilter.class)  // Add JWT filter
        .build();
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    AccessDeniedHandlerImpl handler = new AccessDeniedHandlerImpl();
    handler.setErrorPage("/"); // ⬅️ Przekierowanie na stronę główną
    return handler;
  }
}