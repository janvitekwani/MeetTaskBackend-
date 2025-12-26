package com.example.TaskMeetAI.service;

import com.example.TaskMeetAI.Model.User;
import com.example.TaskMeetAI.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }
        if (user.getCreateAt() == null) {
            user.setCreateAt(new Date());
        }
        return userRepository.save(user);
    }
}