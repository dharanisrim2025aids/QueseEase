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

    // Create Patient
    @PostMapping("/create")
    public ResponseEntity<PatientDTO> createpatient(
            @RequestBody PatientDTO data) {

        Patient patient = convertToEntity(data);
        Patient result = patientservices.createpatient(patient);

        return new ResponseEntity<>(
                convertToDTO(result),
                HttpStatus.CREATED
        );
    }

    // Get All Patients
    @GetMapping("/getall")
    public ResponseEntity<List<PatientDTO>> getallpatient() {

        List<PatientDTO> result = patientservices.getallpatient()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Update Patient
    @PutMapping("/update")
    public ResponseEntity<PatientDTO> updatepatient(
            @RequestBody PatientDTO data) {

        Patient patient = convertToEntity(data);
        Patient result = patientservices.updatepatient(patient);

        return new ResponseEntity<>(
                convertToDTO(result),
                HttpStatus.ACCEPTED
        );
    }

    // Get Patient By ID
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<PatientDTO> getbyid(
            @PathVariable Long id) {

        Patient result = patientservices.getbyid(id);

        return new ResponseEntity<>(
                convertToDTO(result),
                HttpStatus.OK
        );
    }

    // Delete Patient
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletepatient(
            @PathVariable Long id) {

        patientservices.deletepatient(id);

        return new ResponseEntity<>(
                "Patient deleted successfully",
                HttpStatus.OK
        );
    }
}