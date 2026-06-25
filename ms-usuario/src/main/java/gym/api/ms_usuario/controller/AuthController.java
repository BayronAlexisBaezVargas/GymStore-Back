package gym.api.ms_usuario.controller;

import gym.api.ms_usuario.dto.AuthRequest;
import gym.api.ms_usuario.dto.AuthResponse;
import gym.api.ms_usuario.dto.RegisterRequest;
import gym.api.ms_usuario.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe. Por favor, elige otro.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
