package com.caresync.appointment.messaging;

import java.time.LocalDateTime;

public record AppointmentEvent(
        String eventType,
        Long appointmentId,
        Long patientId,
        String patientName,
        String patientEmail,
        Long doctorId,
        String doctorName,
        LocalDateTime dateTime,
        String status
) {
}