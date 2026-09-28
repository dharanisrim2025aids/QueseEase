
package Dharanisri.Project.Controller;

import Dharanisri.Project.DTO.PatientDTO;
import Dharanisri.Project.Models.Patient;
import Dharanisri.Project.Services.PatientServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientServices patientservices;

    // Convert Patient Entity to PatientDTO
    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();

        dto.setId(patient.getId());
        dto.setPatientName(patient.getPatientName());
        dto.setAge(patient.getAge());
        dto.setGender(patient.getGender());
        dto.setContactNumber(patient.getContactNumber());

        return dto;
    }

    // Convert PatientDTO to Patient Entity
    private Patient convertToEntity(PatientDTO dto) {
        Patient patient = new Patient();

        patient.setId(dto.getId());
        patient.setPatientName(dto.getPatientName());
        patient.setAge(dto.getAge());
        patient.setGender(dto.getGender());
        patient.setContactNumber(dto.getContactNumber());

        return patient;
    }

    @PostMapping("/create")
    ResponseEntity<PatientDTO> createpatient(@RequestBody PatientDTO body) {
        Patient patient = convertToEntity(body);
        Patient savedPatient = patientservices.createpatient(patient);

        return new ResponseEntity<>(
                convertToDTO(savedPatient),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/getall")
    ResponseEntity<List<PatientDTO>> getall() {
        List<PatientDTO> patients = patientservices.getallpatient()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @PutMapping("/update")
    ResponseEntity<PatientDTO> updatepatient(@RequestBody PatientDTO data) {
        Patient patient = convertToEntity(data);
        Patient updatedPatient = patientservices.updatepatient(patient);

        return new ResponseEntity<>(
                convertToDTO(updatedPatient),
                HttpStatus.ACCEPTED
        );
    }

    @GetMapping("/getbyid/{id}")
    ResponseEntity<?> getbyId(@PathVariable long id) {
        try {
            Patient response = patientservices.getbyid(id);

            return new ResponseEntity<>(
                    convertToDTO(response),
                    HttpStatus.OK
            );

        } catch (RuntimeException exception) {
            return new ResponseEntity<>("not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> deletepatient(@PathVariable long id) {
        patientservices.deletepatient(id);

        return new ResponseEntity<>(
                "Patient deleted successfully",
                HttpStatus.OK
        );
    }
}