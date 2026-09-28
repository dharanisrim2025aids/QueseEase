package Dharanisri.Project.Controller;

import Dharanisri.Project.DTO.*;
import Dharanisri.Project.Models.Token;
import Dharanisri.Project.Models.Patient;
import Dharanisri.Project.Models.Doctor;
import Dharanisri.Project.Services.TokenServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/token")
@CrossOrigin(origins = "*")
public class TokenController {

    @Autowired
    private TokenServices tokenservices;

    private TokenResponseDTO convertToDTO(Token token) {

        Patient patient = token.getPatient();
        Doctor doctor = token.getDoctor();

        PatientResponseDTO patientDTO = new PatientResponseDTO(
                patient.getId(),
                patient.getPatientName(),
                patient.getAge(),
                patient.getGender(),
                patient.getContactNumber()
        );

        DoctorResponseDTO doctorDTO = new DoctorResponseDTO(
                doctor.getId(),
                doctor.getDoctorName(),
                doctor.getSpecialization(),
                doctor.getAverageConsultationTime(),
                doctor.getCurrentServingToken()
        );

        return new TokenResponseDTO(
                token.getId(),
                token.getTokenNumber(),
                token.getTokenDate(),
                token.isPriority(),
                token.getStatus(),
                token.getEstimatedWaitTime(),
                patientDTO,
                doctorDTO
        );
    }

    @GetMapping("/getall")
    public ResponseEntity<List<TokenResponseDTO>> getalltoken() {

        List<TokenResponseDTO> result =
                tokenservices.getalltoken()
                        .stream()
                        .map(this::convertToDTO)
                        .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<TokenResponseDTO> getbyid(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                convertToDTO(tokenservices.getbyid(id))
        );
    }

    @PostMapping("/generate")
    public ResponseEntity<TokenResponseDTO> generatetoken(
            @RequestParam Long doctorId,
            @RequestParam Long patientId) {

        Token result =
                tokenservices.generatetoken(doctorId, patientId);

        return ResponseEntity.status(201).body(convertToDTO(result));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TokenResponseDTO> updatetoken(
            @PathVariable Long id,
            @RequestBody TokenRequestDTO data) {

        Token token = new Token();

        token.setStatus(data.getStatus());
        token.setPriority(data.isPriority());
        token.setEstimatedWaitTime(data.getEstimatedWaitTime());

        Token result = tokenservices.updatetoken(id, token);

        return ResponseEntity.accepted().body(convertToDTO(result));
    }

    @PostMapping("/priority")
    public ResponseEntity<TokenResponseDTO> makepriority(
            @RequestParam Long doctorId,
            @RequestParam Long patientId) {

        Token result =
                tokenservices.makepriority(doctorId, patientId);

        return ResponseEntity.ok(convertToDTO(result));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletetoken(
            @PathVariable Long id) {

        tokenservices.deletetoken(id);

        return ResponseEntity.ok("Token deleted successfully");
    }
}