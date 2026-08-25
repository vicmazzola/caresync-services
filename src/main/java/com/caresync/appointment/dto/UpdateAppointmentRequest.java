package com.caresync.appointment.dto;

import com.caresync.appointment.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateAppointmentRequest(

        @NotNull
        LocalDateTime dateTime,

        String notes,

        @NotNull
        AppointmentStatus status
) {
}

