package com.radarscene.api.service;

import com.radarscene.api.dto.AuthRequest;
import com.radarscene.api.dto.AuthResponse;
import com.radarscene.api.dto.RegisterRequest;
import com.radarscene.api.model.User;
import com.radarscene.api.repository.UserRepository;
import com.radarscene.api.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Service for authentication operations
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user
     *
     * @param request registration details
     * @return authentication response with tokens
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of("USER"))
                .build();
        
        // Save user to database
        userRepository.save(user);
        
        // Generate tokens
        var accessToken = jwtTokenUtil.generateToken(user);
        var refreshToken = jwtTokenUtil.generateRefreshToken(user);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Authenticate a user
     *
     * @param request authentication credentials
     * @return authentication response with tokens
     */
    public AuthResponse authenticate(AuthRequest request) {
        // Authenticate with Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        // If authentication doesn't throw an exception, user is valid
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        
        // Generate tokens
        var accessToken = jwtTokenUtil.generateToken(user);
        var refreshToken = jwtTokenUtil.generateRefreshToken(user);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Refresh access token using refresh token
     *
     * @param refreshToken current refresh token
     * @return authentication response with new tokens
     */
    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtTokenUtil.extractUsername(refreshToken);
        if (username != null) {
            var user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            if (jwtTokenUtil.isTokenValid(refreshToken, user)) {
                var accessToken = jwtTokenUtil.generateToken(user);
                
                return AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        throw new IllegalArgumentException("Invalid refresh token");
    }
}
