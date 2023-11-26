package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import com.example.demo.repositories.DoctorRepository;
import com.example.demo.repositories.PatientRepository;

import com.example.demo.entities.Doctor;
import com.example.demo.entities.Patient;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class EntityUnitTest {

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Test
    void shouldSaveAndRetrievePatient() {
        // Crea un paciente........
        Patient patient = new Patient("NombrePaciente", "ApellidoPaciente", 25, "emailPaciente@hospital.com");
        
        // Guarda el paciente........
        patientRepository.save(patient);

        // Recupera el paciente por su ID.........
        Patient retrievedPatient = patientRepository.findById(patient.getId()).orElse(null);

        // Verificar que el paciente recuperado no sea nulo y que sus caracteristicas coincidan.........
        assertThat(retrievedPatient).isNotNull();
        assertThat(retrievedPatient.getFirstName()).isEqualTo(patient.getFirstName());
        assertThat(retrievedPatient.getLastName()).isEqualTo(patient.getLastName());
        assertThat(retrievedPatient.getAge()).isEqualTo(patient.getAge());
        assertThat(retrievedPatient.getEmail()).isEqualTo(patient.getEmail());
    }

    @Test
    void shouldSaveAndRetrieveDoctor() {
        // Crea un doctor......
        Doctor doctor = new Doctor("NombreDoctor", "ApellidoDoctor", 30, "emailDoctor@hospital.com");

        // Guarda el doctor.......
        doctorRepository.save(doctor);

        // Recupera el doctor por su ID........
        Doctor retrievedDoctor = doctorRepository.findById(doctor.getId()).orElse(null);

        // Verifica que el doctor recuperado no sea nulo y que sus caracteristicas coincidan.....
        assertThat(retrievedDoctor).isNotNull();
        assertThat(retrievedDoctor.getFirstName()).isEqualTo(doctor.getFirstName());
        assertThat(retrievedDoctor.getLastName()).isEqualTo(doctor.getLastName());
        assertThat(retrievedDoctor.getAge()).isEqualTo(doctor.getAge());
        assertThat(retrievedDoctor.getEmail()).isEqualTo(doctor.getEmail());
    }
}

