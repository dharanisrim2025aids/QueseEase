
package Dharanisri.Project.Controller;

import Dharanisri.Project.DTO.TokenDTO;
import Dharanisri.Project.Models.Token;
import Dharanisri.Project.Models.Doctor;
import Dharanisri.Project.Models.Patient;
import Dharanisri.Project.Services.TokenServices;
import Dharanisri.Project.Services.DoctorServices;
import Dharanisri.Project.Services.PatientServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Autowired
    private TokenServices tokenservices;

    @Autowired
    private DoctorServices doctorservices;

    @Autowired
    private PatientServices patientservices;

    // Convert Token Entity to TokenDTO
    private TokenDTO convertToDTO(Token token) {
        TokenDTO dto = new TokenDTO();

        dto.setId(token.getId());
        dto.setTokenNumber(token.getTokenNumber());
        dto.setTokenDate(token.getTokenDate());
        dto.setIsPriority(token.isIsPriority());
        dto.setStatus(token.getStatus());
        dto.setEstimatedWaitTime(token.getEstimatedWaitTime());

        if (token.getDoctor() != null) {
            dto.setDoctorId(token.getDoctor().getId());
        }

        if (token.getPatient() != null) {
            dto.setPatientId(token.getPatient().getId());
        }

        return dto;
    }

    // Convert TokenDTO to Token Entity
    private Token convertToEntity(TokenDTO dto) {
        Token token = new Token();

        token.setId(dto.getId());
        token.setTokenNumber(dto.getTokenNumber());
        token.setTokenDate(dto.getTokenDate());
        token.setIsPriority(dto.isIsPriority());
        token.setStatus(dto.getStatus());
        token.setEstimatedWaitTime(dto.getEstimatedWaitTime());

        if (dto.getDoctorId() != null) {
            Doctor doctor = doctorservices.getbyid(dto.getDoctorId());
            token.setDoctor(doctor);
        }

        if (dto.getPatientId() != null) {
            Patient patient = patientservices.getbyid(dto.getPatientId());
            token.setPatient(patient);
        }

        return token;
    }

    @PostMapping("/create")
    public ResponseEntity<TokenDTO> createtoken(@RequestBody TokenDTO data) {
        Token token = convertToEntity(data);
        Token result = tokenservices.createtoken(token);

        return new ResponseEntity<>(convertToDTO(result), HttpStatus.CREATED);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<TokenDTO>> getalltoken() {
        List<TokenDTO> result = tokenservices.getalltoken()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<TokenDTO> updatetoken(@RequestBody TokenDTO data) {
        Token token = convertToEntity(data);
        Token result = tokenservices.updatetoken(token);

        return new ResponseEntity<>(convertToDTO(result), HttpStatus.ACCEPTED);
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<TokenDTO> getbyid(@PathVariable Long id) {
        Token result = tokenservices.getbyid(id);

        return new ResponseEntity<>(convertToDTO(result), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletetoken(@PathVariable Long id) {
        tokenservices.deletetoken(id);

        return new ResponseEntity<>(
                "Token deleted successfully",
                HttpStatus.OK
        );
    }

    // Automatic Token Generation
    @PostMapping("/generate")
    public ResponseEntity<TokenDTO> generatetoken(
            @RequestParam Long doctorId,
            @RequestParam Long patientId) {

        Token result = tokenservices.generatetoken(doctorId, patientId);

        return new ResponseEntity<>(convertToDTO(result), HttpStatus.CREATED);
    }

    // Advance Current Serving Token
    @PutMapping("/advance/{doctorId}")
    public ResponseEntity<TokenDTO> advanceToken(
            @PathVariable Long doctorId) {

        Token result = tokenservices.advanceToken(doctorId);

        return new ResponseEntity<>(convertToDTO(result), HttpStatus.OK);
    }

    // Emergency / Priority Token Generation
    @PostMapping("/priority")
    public ResponseEntity<TokenDTO> generatePriorityToken(
            @RequestParam Long doctorId,
            @RequestParam Long patientId) {

        Token result =
                tokenservices.generatePriorityToken(doctorId, patientId);

        return new ResponseEntity<>(
                convertToDTO(result),
                HttpStatus.CREATED
        );
    }

    // Doctor-wise Daily Token History
    @GetMapping("/history/{doctorId}")
    public ResponseEntity<List<TokenDTO>> getDailyHistory(
            @PathVariable Long doctorId) {

        List<TokenDTO> result = tokenservices.getDailyHistory(doctorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}