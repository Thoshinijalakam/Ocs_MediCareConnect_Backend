package com.example.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Bean.Appointment;
import com.example.Bean.AppointmentStatus;
import com.example.Repository.AppointmentRepository;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository repo;

    // ✅ BOOK
    @Override
    public Appointment bookAppointment(Appointment appointment) {

        // 🔴 Prevent duplicate booking
        boolean exists = repo.existsByDoctorAndAppointmentDateAndAppointmentTime(
                appointment.getDoctor(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );

        if (exists) {
            throw new RuntimeException("Slot already booked");
        }

        // ✅ Set default status
        appointment.setStatus(AppointmentStatus.BOOKED);

        return repo.save(appointment);
    }

    // ✅ GET ALL
    @Override
    public List<Appointment> getAllAppointments() {
        return repo.findAll();
    }

    // ✅ GET BY ID
    @Override
    public Appointment getAppointmentById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    // ✅ CANCEL
    @Override
    public void cancelAppointment(Long id) {

        Appointment appt = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment already cancelled");
        }

        if (appt.getStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed appointment");
        }

        appt.setStatus(AppointmentStatus.CANCELLED);
        repo.save(appt);
    }

    // ✅ COMPLETE (NEW)
    @Override
    public void completeAppointment(Long id) {

        Appointment appt = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Cannot complete cancelled appointment");
        }

        if (appt.getStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Already completed");
        }

        appt.setStatus(AppointmentStatus.COMPLETED);
        repo.save(appt);
    }
}