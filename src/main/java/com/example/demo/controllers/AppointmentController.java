package com.example.demo.controllers;

import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.entities.Appointment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Endpoint para obtener todas las citas........
    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        // Obtiene todas las citas desde el repositorio........
        List<Appointment> appointments = appointmentRepository.findAll();

        // Verifica si no hay citas y devuelve un código de respuesta.........
        if (appointments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Devuelve la lista de citas con la respuesta OK........
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    // Endpoint para obtener una cita por ID.......
    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable("id") long id) {
        // Busca la cita por ID en el repositorio........
        Optional<Appointment> appointment = appointmentRepository.findById(id);

        // Verifica si la cita existe y devuelve un código de respuesta correspondiente.............
        if (appointment.isPresent()) {
            return new ResponseEntity<>(appointment.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para crear una nueva cita..........
    @PostMapping("/appointments")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        // Verifica si la fecha de inicio es anterior a la fecha actual.......
        if (appointment.getStartsAt().isBefore(LocalDateTime.now())) {
            //aqui falta lógica ::::::::::((()))
        }

        // Guarda la nueva cita.........
        Appointment createdAppointment = appointmentRepository.save(appointment);

        // Devuelve la nueva cita con la respuesta OK........
        return new ResponseEntity<>(createdAppointment, HttpStatus.OK);
    }

    // Endpoint para eliminar una cita por ID.......
    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<HttpStatus> deleteAppointment(@PathVariable("id") long id) {
        // Verifica si la cita existe y la elimina........
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            // Devuelve la respuesta NOT_FOUND si la cita no existe.........
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para eliminar todas las citas............
    @DeleteMapping("/appointments")
    public ResponseEntity<HttpStatus> deleteAllAppointments() {
        // Elimina todas las citas..........
        appointmentRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

