package com.example.demo;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.controllers.AppointmentController;
import com.example.demo.entities.Appointment;
import com.example.demo.entities.Doctor;
import com.example.demo.entities.Patient;
import com.example.demo.entities.Room;
import com.example.demo.repositories.AppointmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerUnitTest {

    @MockBean
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAppointment() throws Exception {
        // Crea instancias de paciente, doctor, sala y cita.............
        Patient patient = new Patient("Jose Luis", "Olaya", 37, "j.olaya@email.com");
        Doctor doctor = new Doctor("Perla", "Amalia", 24, "p.amalia@hospital.accwe");
        Room room = new Room("Dermatology");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        LocalDateTime startsAt = LocalDateTime.parse("19:30 24/04/2023", formatter);
        LocalDateTime finishesAt = LocalDateTime.parse("20:30 24/04/2023", formatter);

        Appointment appointment = new Appointment(patient, doctor, room, startsAt, finishesAt);

        // Realizar la solicitud POST para crear una cita
        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointment)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotCreateAppointment() throws Exception {
        // Crea instancias de paciente, doctor, sala y cita con fechas de inicio y fin iguales...........
        Patient patient = new Patient("Jose Luis", "Olaya", 37, "j.olaya@email.com");
        Doctor doctor = new Doctor("Perla", "Amalia", 24, "p.amalia@hospital.accwe");
        Room room = new Room("Dermatology");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        LocalDateTime startsAt = LocalDateTime.parse("19:30 24/04/2023", formatter);
        LocalDateTime finishesAt = LocalDateTime.parse("19:30 24/04/2023", formatter);

        Appointment appointment = new Appointment(patient, doctor, room, startsAt, finishesAt);

        // Realiza la solicitud POST para crear una cita con fechas de inicio y fin iguales.............
        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateOneAppointmentOutOfTwoConflictDate() throws Exception {
        // Crea instancias de paciente, doctor, sala y dos citas con fechas conflictivas........
        Patient patient = new Patient("Jose Luis", "Olaya", 37, "j.olaya@email.com");
        Patient patient2 = new Patient("Paulino", "Antunez", 37, "p.antunez@email.com");
        Doctor doctor = new Doctor("Perla", "Amalia", 24, "p.amalia@hospital.accwe");
        Doctor doctor2 = new Doctor("Miren", "Iniesta", 24, "m.iniesta@hospital.accwe");
        Room room = new Room("Dermatology");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        LocalDateTime startsAt = LocalDateTime.parse("19:30 24/04/2023", formatter);
        LocalDateTime finishesAt = LocalDateTime.parse("20:30 24/04/2023", formatter);

        Appointment appointment = new Appointment(patient, doctor, room, startsAt, finishesAt);
        Appointment appointment2 = new Appointment(patient2, doctor2, room, startsAt, finishesAt);

        // Realiza la solicitud POST para crear la primera cita..........
        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointment2)))
                .andExpect(status().isConflict());

        List<Appointment> appointments = new ArrayList<>();
        appointments.add(appointment);

        when(appointmentRepository.findAll()).thenReturn(appointments);

        // Realiza la solicitud POST para crear la segunda cita y verificar que no es aceptable.........
        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointment2)))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldCreateBothAppointmentsConflictDateButNotRoom() throws Exception {
        // Crea instancias de paciente, doctor, salas y dos citas con fechas conflictivas pero salas diferentes........
        Patient patient = new Patient("Jose Luis", "Olaya", 37, "j.olaya@email.com");
        Patient patient2 = new Patient("Paulino", "Antunez", 37, "p.antunez@email.com");
        Doctor doctor = new Doctor("Perla", "Amalia", 24, "p.amalia@hospital.accwe");
        Doctor doctor2 = new Doctor("Miren", "Iniesta", 24, "m.iniesta@hospital.accwe");
        Room room = new Room("Dermatology");
        Room room2 = new Room("Oncology");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        LocalDateTime startsAt = LocalDateTime.parse("19:30 24/04/2023", formatter);
        LocalDateTime finishesAt = LocalDateTime.parse("20:30 24/04/2023", formatter);

        doctor2.setId(2);
        patient2.setId(2);

        Appointment appointment = new Appointment(patient, doctor, room, startsAt, finishesAt);
        Appointment appointment2 = new Appointment(patient2, doctor2, room2, startsAt, finishesAt);

        // Realiza la solicitud POST para crear la primera cita.......
        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointment)))
                .andExpect(status().isOk());

        List<Appointment> appointments = new ArrayList<>();
        appointments.add(appointment);

        when(appointmentRepository.findAll()).thenReturn(appointments);

        // Realiza la solicitud POST para crear la segunda cita y verificar que es aceptable..........
        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointment2)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetNoAppointments() throws Exception {
        // devolve una lista vacía......
        List<Appointment> appointments = new ArrayList<>();
        when(appointmentRepository.findAll()).thenReturn(appointments);

        
    }
}
