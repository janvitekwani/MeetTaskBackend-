package com.example.TaskMeetAI.service;


import com.example.TaskMeetAI.dto.LoginRequest;
import com.example.TaskMeetAI.dto.RegisterRequest;


import com.example.TaskMeetAI.dto.LoginResponse;
import com.example.TaskMeetAI.dto.UserResponse;
import com.example.TaskMeetAI.Model.User;
import com.example.TaskMeetAI.Repository.UserRepository;
import com.example.TaskMeetAI.security.JwtTokenProvider;
import com.example.TaskMeetAI.security.JwtTokenProvider;
import com.example.TaskMeetAI.service.AuthService;
import com.example.TaskMeetAI.service.EmailService;
import com.example.TaskMeetAI.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists with this email");
        }

        // Generate verification token
        String verificationToken = tokenGenerator.generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        // Create user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsEmailVerified(false);
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationExpires(expiresAt);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. Authenticate using Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        System.out.println("Authentication successful for email: " + request.getEmail());

        // 2. Set authentication in context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate JWT
        String token = jwtTokenProvider.generateToken(request.getEmail());

        System.out.println("Generated token: " + token);

        // 4. Build response
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getIsEmailVerified()
        );

        return new LoginResponse(token, userResponse);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        // Check if token is expired
        if (user.getEmailVerificationExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired");
        }

        // Verify email
        user.setIsEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpires(null);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            // Don't reveal if user exists for security
            return;
        }

        User user = userOpt.get();

        // Generate reset token
        String resetToken = tokenGenerator.generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        user.setResetPasswordToken(resetToken);
        user.setResetPasswordExpires(expiresAt);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Send reset email
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        System.out.print(" "+user.getEmail()+" "+resetToken+" ");
    }

    @Override
    @Transactional
    public void resetPassword(String token, String password) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        // Check if token is expired
        if (user.getResetPasswordExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(password));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpires(null);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getIsEmailVerified()
        );
    }
}
