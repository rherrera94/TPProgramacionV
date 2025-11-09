package com.example.demo.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestParam String username, // 2. Cambia el tipo de retorno
                                               @RequestParam String password,
                                               HttpSession session) {
        try {
            // 🔐 Autentica el usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // ✅ Guarda la sesión
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // 🟢 Devuelve 200 OK con el cuerpo de la respuesta
            return ResponseEntity.ok(
                    new LoginResponse("Sesión iniciada correctamente", session.getId())
            );

        } catch (AuthenticationException e) {
            // 🔴 Si las credenciales no son válidas
            System.out.println("Error de autenticación: " + e.getMessage()); // Corregí tu System.out

            // ⬇️ ESTA ES LA LÍNEA CLAVE ⬇️
            // Devuelve 401 UNAUTHORIZED con el cuerpo de error
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("Error: credenciales inválidas", null));
        }
    }

    // ✅ Endpoint opcional para cerrar sesión
    @PostMapping("/logout")
    public LoginResponse logout(HttpSession session) {
        session.invalidate();
        return new LoginResponse("Sesión cerrada correctamente", null);
    }
    @GetMapping("/generate")
    public String generatePassword() {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        return encoder.encode("1234");
    }


    // 📦 Clase interna para la respuesta
    public record LoginResponse(String mensaje, String sessionId) {}
}
