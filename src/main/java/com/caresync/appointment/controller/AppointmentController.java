package com.caresync.appointment.controller;

import com.caresync.appointment.dto.AppointmentResponse;
import com.caresync.appointment.dto.CreateAppointmentRequest;
import com.caresync.appointment.dto.UpdateAppointmentRequest;
import com.caresync.appointment.entity.Appointment;
import com.caresync.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request
    ) {
        Appointment appointment = appointmentService.createAppointment(
                request.patientId(),
                request.doctorId(),
                request.dateTime(),
                request.notes()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AppointmentResponse.from(appointment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentRequest request
    ) {
        Appointment appointment = appointmentService.updateAppointment(
                id,
                request.dateTime(),
                request.notes(),
                request.status()
        );

        return ResponseEntity.ok(
                AppointmentResponse.from(appointment)
        );
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointments(
            @PathVariable Long patientId
    ) {
        List<AppointmentResponse> appointments =
                appointmentService.getPatientAppointments(patientId)
                        .stream()
                        .map(AppointmentResponse::from)
                        .toList();

        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/patient/{patientId}/future")
    public ResponseEntity<List<AppointmentResponse>> getFuturePatientAppointments(
            @PathVariable Long patientId
    ) {
        List<AppointmentResponse> appointments =
                appointmentService.getFuturePatientAppointments(patientId)
                        .stream()
                        .map(AppointmentResponse::from)
                        .toList();

        return ResponseEntity.ok(appointments);
    }
}