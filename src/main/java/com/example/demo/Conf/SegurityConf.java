package com.example.demo.conf; // (PD: El nombre 'SegurityConf' tiene un typo, suele ser 'SecurityConf')

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // 1. ¡IMPORTANTE AGREGAR ESTO!
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SegurityConf { // (El typo está acá)

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ... (Tu 'userDetailsService' está perfecto, no lo toco) ...
    @Bean
    public UserDetailsService userDetailsService(com.example.demo.repository.UsuarioRepository usuarioRepository) {
        return username -> usuarioRepository.findByUsername(username)
                .map(usuario -> {
                    System.out.println("--- DEBUG SPRING SECURITY ---");
                    System.out.println("Logueando usuario: " + usuario.getUsername());
                    List<SimpleGrantedAuthority> authorities = usuario.getRoles().stream()
                            .map(rol -> {
                                String nombreRol = rol.getNombre();
                                System.out.println(">>> Rol encontrado en BD: '" + nombreRol + "'"); // LA LÍNEA CLAVE
                                return new SimpleGrantedAuthority(nombreRol);
                            })
                            .collect(Collectors.toList());

                    System.out.println(">>> Roles cargados en Spring: " + authorities);
                    System.out.println("---------------------------------");
                    return new org.springframework.security.core.userdetails.User(
                            usuario.getUsername(),
                            usuario.getPassword(),
                            usuario.getRoles().stream()
                                    .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                                    .collect(Collectors.toList())
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    // ... (Tu 'authenticationProvider' está perfecto) ...
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    // ... (Tu 'authenticationManager' está perfecto) ...
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // 1. ENDPOINTS PÚBLICOS
                        .requestMatchers("/auth/login", "/error", "/auth/generate").permitAll()

                        // 2. ENDPOINTS DE USUARIOS (Solo Admin)
                        .requestMatchers(HttpMethod.POST, "/api/usuario/add").hasRole("ADMIN") // Busca ROLE_ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/usuario/listar").hasAnyRole("ADMIN", "USER") // Busca ROLE_ADMIN

                        // 3. ENDPOINTS DE ARTÍCULOS
                        // Lectura (USER y ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/articulo/listar", "/api/articulo/listar/**", "/api/articulo/buscar/nombre/**").hasAnyRole("ADMIN", "USER") // Busca ROLE_ADMIN o ROLE_USER
                        // Escritura (Solo ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/articulo/add").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/articulo/update").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/articulo/delete/**").hasRole("ADMIN")

                        // 4. ENDPOINTS DE PERSONAS
                        // Lectura (USER y ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/persona/listar", "/api/persona/listarporid/**", "/api/persona/buscar/email/**", "/api/persona/buscar/nombre/**").hasAnyRole("ADMIN", "USER")
                        // Escritura (Solo ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/persona/add").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/persona/actualizar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/persona/eliminar/**").hasRole("ADMIN")

                        // 5. ENDPOINTS DE RESERVAS
                        // Lectura y Creación (USER y ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/reservas/listar", "/api/reservas/listar/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/reservas/crear").hasAnyRole("ADMIN", "USER")
                        // Modificación y Borrado (Solo ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/reservas/actualizar/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservas/borrar/**").hasRole("ADMIN")

                        // 6. REGLA FINAL
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout
                        .logoutUrl("/logout") // La URL que llamás
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Le decimos que devuelva 200 OK y no una redirección
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .permitAll()
                );
                //.logout(logout -> logout.permitAll());

        return http.build();
    }



//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//
//                        // 1. ENDPOINTS PÚBLICOS
//                        .requestMatchers("/auth/login", "/error", "/auth/generate").permitAll()
//
//                        // 2. ENDPOINTS DE USUARIOS (Solo Admin)
//                        .requestMatchers(HttpMethod.POST, "/api/usuario/add").hasAuthority("ADMIN") // <-- CAMBIO
//                        .requestMatchers(HttpMethod.GET, "/api/usuario/listar").hasAuthority("ADMIN") // <-- CAMBIO
//
//                        // 3. ENDPOINTS DE ARTÍCULOS
//                        // Lectura (USER y ADMIN)
//                        .requestMatchers(HttpMethod.GET, "/api/articulo/listar", "/api/articulo/listar/**", "/api/articulo/buscar/nombre/**").hasAnyAuthority("ADMIN", "USER") // <-- CAMBIO
//                        // Escritura (Solo ADMIN)
//                        .requestMatchers(HttpMethod.POST, "/api/articulo/add").hasAuthority("ADMIN") // <-- CAMBIO
//                        .requestMatchers(HttpMethod.PUT, "/api/articulo/update").hasAuthority("ADMIN") // <-- CAMBIO
//                        .requestMatchers(HttpMethod.DELETE, "/api/articulo/delete/**").hasAuthority("ADMIN") // <-- CAMBIO
//
//                        // 4. ENDPOINTS DE PERSONAS
//                        // Lectura (USER y ADMIN)
//                        .requestMatchers(HttpMethod.GET, "/api/persona/listar", "/api/persona/listarporid/**", "/api/persona/buscar/email/**", "/api/persona/buscar/nombre/**").hasAnyAuthority("ADMIN", "USER") // <-- CAMBIO
//                        // Escritura (Solo ADMIN)
//                        .requestMatchers(HttpMethod.POST, "/api/persona/add").hasAuthority("ADMIN") // <-- CAMBIO
//                        .requestMatchers(HttpMethod.PUT, "/api/persona/actualizar").hasAuthority("ADMIN") // <-- CAMBIO
//                        .requestMatchers(HttpMethod.DELETE, "/api/persona/eliminar/**").hasAuthority("ADMIN") // <-- CAMBIO
//
//                        // 5. ENDPOINTS DE RESERVAS
//                        // Lectura y Creación (USER y ADMIN)
//                        .requestMatchers(HttpMethod.GET, "/api/reservas/listar", "/api/reservas/listar/**").hasAnyAuthority("ADMIN", "USER") // <-- CAMBIO
//                        .requestMatchers(HttpMethod.POST, "/api/reservas/crear").hasAnyAuthority("ADMIN", "USER") // <-- CAMBIO
//                        // Modificación y Borrado (Solo ADMIN)
//                        .requestMatchers(HttpMethod.PUT, "/api/reservas/actualizar/**").hasAuthority("ADMIN") // <-- CAMBIO
//                        .requestMatchers(HttpMethod.DELETE, "/api/reservas/borrar/**").hasAuthority("ADMIN") // <-- CAMBIO
//
//                        // 6. REGLA FINAL
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form.disable())
//                .httpBasic(basic -> basic.disable())
//                .logout(logout -> logout.permitAll());
//
//        return http.build();
//    }


//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // Necesario para Postman y tu front de Flask
//                .authorizeHttpRequests(auth -> auth
//
//                        // 1. ENDPOINTS PÚBLICOS (Sin autenticación)
//                        .requestMatchers("/auth/login", "/error", "/auth/generate").permitAll()
//
//                        // 2. ENDPOINTS DE USUARIOS (Solo Admin)
//                        .requestMatchers(HttpMethod.POST, "/api/usuario/add").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/api/usuario/listar").hasRole("ADMIN")
//
//                        // 3. ENDPOINTS DE ARTÍCULOS
//                        // Lectura (USER y ADMIN)
//                        .requestMatchers(HttpMethod.GET, "/api/articulo/listar", "/api/articulo/listar/**", "/api/articulo/buscar/nombre/**").hasAnyRole("ADMIN", "USER")
//                        // Escritura (Solo ADMIN)
//                        .requestMatchers(HttpMethod.POST, "/api/articulo/add").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/api/articulo/update").hasRole("ADMIN") // Asumo que tu ruta es 'update'
//                        .requestMatchers(HttpMethod.DELETE, "/api/articulo/delete/**").hasRole("ADMIN")
//
//                        // 4. ENDPOINTS DE PERSONAS (¡CON LOS NUEVOS!)
//                        // Lectura (USER y ADMIN)
//                        .requestMatchers(HttpMethod.GET, "/api/persona/listar", "/api/persona/listarporid/**", "/api/persona/buscar/email/**", "/api/persona/buscar/nombre/**").hasAnyRole("ADMIN", "USER")
//                        // Escritura (Solo ADMIN)
//                        .requestMatchers(HttpMethod.POST, "/api/persona/add").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/api/persona/actualizar").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/persona/eliminar/**").hasRole("ADMIN")
//
//                        // 5. ENDPOINTS DE RESERVAS (¡CORREGIDO!)
//                        // Lectura y Creación (USER y ADMIN)
//                        .requestMatchers(HttpMethod.GET, "/api/reservas/listar", "/api/reservas/listar/**").hasAnyRole("ADMIN", "USER")
//                        .requestMatchers(HttpMethod.POST, "/api/reservas/crear").hasAnyRole("ADMIN", "USER")
//                        // Modificación y Borrado (Solo ADMIN)
//                        .requestMatchers(HttpMethod.PUT, "/api/reservas/actualizar/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/reservas/borrar/**").hasRole("ADMIN")
//
//                        // 6. REGLA FINAL
//                        .anyRequest().authenticated() // Cualquier otra ruta requiere estar logueado
//                )
//                .formLogin(form -> form.disable()) // Deshabilitamos login por formulario
//                .httpBasic(basic -> basic.disable()) // Deshabilitamos Basic Auth
//                .logout(logout -> logout.permitAll()); // Permitimos que /logout sea accesible
//
//        return http.build();
//    }
}