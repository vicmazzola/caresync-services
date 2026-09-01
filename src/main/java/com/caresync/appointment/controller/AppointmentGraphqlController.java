package com.caresync.appointment.controller;

import com.caresync.appointment.dto.AppointmentResponse;
import com.caresync.appointment.service.AppointmentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AppointmentGraphqlController {

    private final AppointmentService appointmentService;

    public AppointmentGraphqlController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @QueryMapping
    public List<AppointmentResponse> patientAppointments(
            @Argument Long patientId,
            Authentication authentication
    ) {
        boolean isPatient = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_PATIENT"::equals);

        return appointmentService.getPatientAppointments(
                        patientId,
                        authentication.getName(),
                        isPatient
                )
                .stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    @QueryMapping
    public List<AppointmentResponse> futurePatientAppointments(
            @Argument Long patientId,
            Authentication authentication
    ) {
        boolean isPatient = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_PATIENT"::equals);

        return appointmentService.getFuturePatientAppointments(
                        patientId,
                        authentication.getName(),
                        isPatient
                )
                .stream()
                .map(AppointmentResponse::from)
                .toList();
    }
}