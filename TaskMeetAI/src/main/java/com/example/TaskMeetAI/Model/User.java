package com.example.TaskMeetAI.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @Field("name")
    private String name;

    @Indexed(unique = true)
    @Field("email")
    private String email;

    @Field("password")
    private String password; // Hashed password

    @Field("google_id")
    private String googleId; // For OAuth users

    @Field("is_email_verified")
    private Boolean isEmailVerified = false;

    @Field("email_verification_token")
    private String emailVerificationToken;

    @Field("email_verification_expires")
    private LocalDateTime emailVerificationExpires;

   @Field("reset_password_token")
    private String resetPasswordToken;

    @Field("reset_password_expires")
    private LocalDateTime resetPasswordExpires;

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Field("roles")
    private String role = "USER"; // Default role
}