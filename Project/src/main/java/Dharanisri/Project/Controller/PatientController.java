package Dharanisri.Project.Controller;

import Dharanisri.Project.DTO.PatientRequestDTO;
import Dharanisri.Project.DTO.PatientResponseDTO;
import Dharanisri.Project.Models.Patient;
import Dharanisri.Project.Services.PatientServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientServices patientservices;

    private PatientResponseDTO convertToDTO(Patient patient) {
        return new PatientResponseDTO(
                patient.getId(),
                patient.getPatientName(),
                patient.getAge(),
                patient.getGender(),
                patient.getContactNumber()
        );
    }

    private Patient convertToEntity(PatientRequestDTO dto) {
        Patient patient = new Patient();

        patient.setId(dto.getId());
        patient.setPatientName(dto.getPatientName());
        patient.setAge(dto.getAge());
        patient.setGender(dto.getGender());
        patient.setContactNumber(dto.getContactNumber());

        return patient;
    }

    @PostMapping("/create")
    public ResponseEntity<PatientResponseDTO> createpatient(
            @RequestBody PatientRequestDTO data) {

        Patient result =
                patientservices.createpatient(convertToEntity(data));

        return new ResponseEntity<>(
                convertToDTO(result),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/getall")
    public ResponseEntity<List<PatientResponseDTO>> getallpatient() {

        List<PatientResponseDTO> result =
                patientservices.getallpatient()
                        .stream()
                        .map(this::convertToDTO)
                        .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<PatientResponseDTO> getbyid(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                convertToDTO(patientservices.getbyid(id))
        );
    }

    @PutMapping("/update")
    public ResponseEntity<PatientResponseDTO> updatepatient(
            @RequestBody PatientRequestDTO data) {

        Patient result =
                patientservices.updatepatient(convertToEntity(data));

        return ResponseEntity.accepted().body(convertToDTO(result));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletepatient(
            @PathVariable Long id) {

        patientservices.deletepatient(id);

        return ResponseEntity.ok("Patient deleted successfully");
    }
}