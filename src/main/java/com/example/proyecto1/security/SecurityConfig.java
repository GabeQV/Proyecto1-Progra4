package com.example.proyecto1.security;

import org.springframework.beans.factory.annotation.Autowired;
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

    // Inyectamos nuestro manejador personalizado
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/login", "/registro-empresa", "/RegistroOferente", "/css/**", "/js/**", "/images/**","/SaveOfer").permitAll()
                        // ------ AÑADIR REGLAS PARA LOS NUEVOS DASHBOARDS ------
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/empresa/**").hasRole("EMPRESA")
                        .requestMatchers("/oferente/**").hasRole("OFERENTE")
                        // ----------------------------------------------------
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        // ------ USAR NUESTRO MANEJADOR PERSONALIZADO ------
                        .successHandler(customAuthenticationSuccessHandler)
                        // ----------------------------------------------------
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") // Redirigir a login con mensaje de logout
                        .permitAll()
                );
        return http.build();
    }
}