package com.nilecare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                // Public pages
                .antMatchers("/", "/login", "/register", "/auth/**").permitAll()
                .antMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                
                // Role-based access
                .antMatchers("/student/**", "/learning/**", "/progress/**", "/support/**", 
                            "/counseling/**", "/assessment/**", "/chat/**", "/feedback/**").hasRole("STUDENT")
                .antMatchers("/counselor/**").hasRole("COUNSELOR")
                .antMatchers("/admin/**").hasRole("ADMIN")
                
                // Authenticated pages
                .antMatchers("/profile/**", "/settings/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            .and()
            .csrf()
                .ignoringAntMatchers("/api/**"); // Optional: if you have REST APIs
        
        return http.build();
    }
}
