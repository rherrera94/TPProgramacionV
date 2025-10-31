package com.example.demo;

import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.Rol_Repositorio;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner seedUsers(Rol_Repositorio rolRepositorio,
                                UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder) {
        return args -> {
            Rol adminRole = rolRepositorio.findByNombre("ROLE_ADMIN");
            //System.out.println("PROBANDO role_admin");
            //System.out.println(adminRole.getNombre());
            if (adminRole == null) {
                adminRole = new Rol();
                adminRole.setNombre("ROLE_ADMIN");
                adminRole = rolRepositorio.save(adminRole);
            }

            Rol userRole = rolRepositorio.findByNombre("ROLE_USER");
            //System.out.println("PROBANDO PROBANDO PROBANDO");
            //System.out.println(userRole.getNombre());
            if (userRole == null) {
                userRole = new Rol();
                userRole.setNombre("ROLE_USER");
                userRole = rolRepositorio.save(userRole);
            }

            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                //System.out.println("PROBANDO username admin");
                //System.out.println(userRole.getNombre());
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("1234"));
                admin.setEnabled(true);
                admin.getRoles().add(adminRole);
                //admin.getRoles().add(userRole);
                usuarioRepository.save(admin);
            }

            if (usuarioRepository.findByUsername("user").isEmpty()) {
                //System.out.println("PROBANDO PROBANDO PROBANDO");
                //System.out.println(userRole.getNombre());
                Usuario user = new Usuario();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("1234"));
                user.setEnabled(true);
                user.getRoles().add(userRole);
                usuarioRepository.save(user);
            }
        };
    }
}