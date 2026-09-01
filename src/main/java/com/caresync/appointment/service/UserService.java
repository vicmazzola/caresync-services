package com.caresync.appointment.service;

import com.caresync.appointment.dto.CreateUserRequest;
import com.caresync.appointment.entity.User;
import com.caresync.appointment.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(CreateUserRequest request) {

        userRepository.findByEmail(request.email())
                .ifPresent(user -> {
                    throw new IllegalArgumentException(
                            "Email already registered"
                    );
                });

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user.setRole(request.role());

        return userRepository.save(user);
    }
}