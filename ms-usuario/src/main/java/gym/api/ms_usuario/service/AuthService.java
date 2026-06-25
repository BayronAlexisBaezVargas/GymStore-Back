package gym.api.ms_usuario.service;

import gym.api.ms_usuario.dto.AuthRequest;
import gym.api.ms_usuario.dto.AuthResponse;
import gym.api.ms_usuario.dto.RegisterRequest;
import gym.api.ms_usuario.model.Role;
import gym.api.ms_usuario.model.User;
import gym.api.ms_usuario.repository.UserRepository;
import gym.api.ms_usuario.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
