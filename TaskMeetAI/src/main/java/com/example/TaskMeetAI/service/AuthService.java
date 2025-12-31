package com.example.TaskMeetAI.service;

import com.example.TaskMeetAI.dto.UserResponse;
import com.example.TaskMeetAI.dto.LoginResponse;
import com.example.TaskMeetAI.dto.RegisterRequest;
import com.example.TaskMeetAI.dto.LoginRequest;

public interface AuthService {
    void register(RegisterRequest request);
   LoginResponse login(LoginRequest request);
    void verifyEmail(String token);
    void forgotPassword(String email);
    void resetPassword(String token, String password);
    UserResponse getCurrentUser(String email);
}