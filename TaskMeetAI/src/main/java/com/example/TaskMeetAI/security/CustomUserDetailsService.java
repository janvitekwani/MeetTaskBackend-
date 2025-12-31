package com.example.TaskMeetAI.security;






import com.example.TaskMeetAI.Model.User;
import com.example.TaskMeetAI.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));

        Boolean isVerified =
                user.getIsEmailVerified() != null && user.getIsEmailVerified();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .disabled(!isVerified)
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String role = user.getRole() != null ? user.getRole() : "USER";
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role)
        );
    }
}

/*import com.example.TaskMeetAI.Model.User;
import com.example.TaskMeetAI.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;






@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Null-safe check for email verification
        Boolean isVerified = user.getIsEmailVerified() != null ? user.getIsEmailVerified() : false;

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!isVerified)  // Use the null-safe boolean value
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String role = user.getRole() != null ? user.getRole() : "USER";
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }
}*/