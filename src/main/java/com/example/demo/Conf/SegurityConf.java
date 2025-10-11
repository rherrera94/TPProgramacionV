package com.example.demo.Conf;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
public class SegurityConf {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService (PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("1234"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(encoder.encode("1234"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("api/persona/add", "api/persona/actualizar", "api/persona/eliminar/**").hasRole("ADMIN")
                        .requestMatchers("/api/persona/listar", "/api/persona/listarporid/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/articulo/add", "/api/articulo/actualizar", "/api/articulo/eliminar/**").hasRole("ADMIN")
                        .requestMatchers("/api/articulo/listar", "/api/articulo/listar/**", "/api/articulo/buscarpornombre/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {})
                .formLogin(form -> form.disable());

        return http.build();

    }

}