package com.example.TaskMeetAI.Repository;

import com.example.TaskMeetAI.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByResetPasswordToken(String token);

    Optional<User> findByGoogleId(String googleId);

    boolean existsByEmail(String email);
}