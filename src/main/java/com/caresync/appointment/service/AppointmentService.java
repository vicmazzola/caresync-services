package com.caresync.appointment.service;

import com.caresync.appointment.messaging.AppointmentEvent;
import com.caresync.appointment.messaging.AppointmentEventPublisher;
import com.caresync.appointment.entity.Appointment;
import com.caresync.appointment.repository.AppointmentRepository;
import com.caresync.appointment.entity.AppointmentStatus;
import com.caresync.appointment.entity.User;
import com.caresync.appointment.repository.UserRepository;
import com.caresync.appointment.entity.UserRole;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final AppointmentEventPublisher appointmentEventPublisher;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            AppointmentEventPublisher appointmentEventPublisher
    ) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.appointmentEventPublisher = appointmentEventPublisher;
    }

    public Appointment createAppointment(
            Long patientId,
            Long doctorId,
            LocalDateTime dateTime,
            String notes
    ) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        if (patient.getRole() != UserRole.PATIENT) {
            throw new IllegalArgumentException("Selected user is not a patient");
        }

        if (doctor.getRole() != UserRole.DOCTOR) {
            throw new IllegalArgumentException("Selected user is not a doctor");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDateTime(dateTime);
        appointment.setNotes(notes);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        appointmentEventPublisher.publish(
                new AppointmentEvent(
                        "CREATED",
                        savedAppointment.getId(),
                        patient.getId(),
                        patient.getName(),
                        patient.getEmail(),
                        doctor.getId(),
                        doctor.getName(),
                        savedAppointment.getDateTime(),
                        savedAppointment.getStatus().name()
                )
        );

        return savedAppointment;
    }

    public Appointment updateAppointment(
            Long appointmentId,
            LocalDateTime dateTime,
            String notes,
            AppointmentStatus status
    ) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        appointment.setDateTime(dateTime);
        appointment.setNotes(notes);
        appointment.setStatus(status);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        appointmentEventPublisher.publish(
                new AppointmentEvent(
                        "UPDATED",
                        savedAppointment.getId(),
                        savedAppointment.getPatient().getId(),
                        savedAppointment.getPatient().getName(),
                        savedAppointment.getPatient().getEmail(),
                        savedAppointment.getDoctor().getId(),
                        savedAppointment.getDoctor().getName(),
                        savedAppointment.getDateTime(),
                        savedAppointment.getStatus().name()
                )
        );

        return savedAppointment;
    }

    public List<Appointment> getPatientAppointments(
            Long patientId,
            String authenticatedEmail,
            boolean isPatient
    ) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        validatePatientAccess(patient, authenticatedEmail, isPatient);

        return appointmentRepository.findByPatient(patient);
    }

    public List<Appointment> getFuturePatientAppointments(
            Long patientId,
            String authenticatedEmail,
            boolean isPatient
    ) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        validatePatientAccess(patient, authenticatedEmail, isPatient);

        return appointmentRepository.findByPatientAndDateTimeAfter(
                patient,
                LocalDateTime.now()
        );
    }

    private void validatePatientAccess(
            User patient,
            String authenticatedEmail,
            boolean isPatient
    ) {
        if (isPatient && !patient.getEmail().equals(authenticatedEmail)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Patients can only access their own appointments"
            );
        }
    }

}

