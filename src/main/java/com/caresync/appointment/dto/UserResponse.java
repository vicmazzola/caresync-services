package com.caresync.appointment.dto;

import com.caresync.appointment.entity.User;
import com.caresync.appointment.entity.UserRole;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}