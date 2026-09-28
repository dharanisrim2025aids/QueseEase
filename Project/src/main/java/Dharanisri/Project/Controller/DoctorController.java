package Dharanisri.Project.Controller;

import Dharanisri.Project.DTO.DoctorRequestDTO;
import Dharanisri.Project.DTO.DoctorResponseDTO;
import Dharanisri.Project.Models.Doctor;
import Dharanisri.Project.Services.DoctorServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorServices doctorservices;

    private DoctorResponseDTO convertToDTO(Doctor doctor) {
        return new DoctorResponseDTO(
                doctor.getId(),
                doctor.getDoctorName(),
                doctor.getSpecialization(),
                doctor.getAverageConsultationTime(),
                doctor.getCurrentServingToken()
        );
    }

    private Doctor convertToEntity(DoctorRequestDTO dto) {
        Doctor doctor = new Doctor();

        doctor.setId(dto.getId());
        doctor.setDoctorName(dto.getDoctorName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setAverageConsultationTime(
                dto.getAverageConsultationTime()
        );
        doctor.setCurrentServingToken(dto.getCurrentServingToken());

        return doctor;
    }

    @PostMapping("/create")
    public ResponseEntity<DoctorResponseDTO> createdoctor(
            @RequestBody DoctorRequestDTO data) {

        Doctor result =
                doctorservices.createdoctor(convertToEntity(data));

        return ResponseEntity.status(201).body(convertToDTO(result));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<DoctorResponseDTO>> getalldoctor() {

        List<DoctorResponseDTO> result =
                doctorservices.getalldoctor()
                        .stream()
                        .map(this::convertToDTO)
                        .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<DoctorResponseDTO> getbyid(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                convertToDTO(doctorservices.getbyid(id))
        );
    }

    @PutMapping("/update")
    public ResponseEntity<DoctorResponseDTO> updatedoctor(
            @RequestBody DoctorRequestDTO data) {

        Doctor result =
                doctorservices.updatedoctor(convertToEntity(data));

        return ResponseEntity.accepted().body(convertToDTO(result));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletedoctor(
            @PathVariable Long id) {

        doctorservices.deletedoctor(id);

        return ResponseEntity.ok("Doctor deleted successfully");
    }
}