package com.caresync.appointment.domain.appointment;

import com.caresync.appointment.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient(User patient);

    List<Appointment> findByPatientAndDateTimeAfter(
            User patient,
            LocalDateTime dateTime
    );
}