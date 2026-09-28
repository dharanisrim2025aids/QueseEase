
package Dharanisri.Project.Controller;

import Dharanisri.Project.DTO.DoctorDTO;
import Dharanisri.Project.Models.Doctor;
import Dharanisri.Project.Services.DoctorServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorServices doctorservices;

    // Convert Doctor Entity to DoctorDTO
    private DoctorDTO convertToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();

        dto.setId(doctor.getId());
        dto.setDoctorName(doctor.getDoctorName());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setAverageConsultationTime(doctor.getAverageConsultationTime());
        dto.setCurrentServingToken(doctor.getCurrentServingToken());

        return dto;
    }

    // Convert DoctorDTO to Doctor Entity
    private Doctor convertToEntity(DoctorDTO dto) {
        Doctor doctor = new Doctor();

        doctor.setId(dto.getId());
        doctor.setDoctorName(dto.getDoctorName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setAverageConsultationTime(dto.getAverageConsultationTime());
        doctor.setCurrentServingToken(dto.getCurrentServingToken());

        return doctor;
    }

    @PostMapping("/create")
    ResponseEntity<DoctorDTO> createdoctor(@RequestBody DoctorDTO body) {
        Doctor doctor = convertToEntity(body);
        Doctor savedDoctor = doctorservices.createdoctor(doctor);

        return new ResponseEntity<>(
                convertToDTO(savedDoctor),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/getall")
    ResponseEntity<List<DoctorDTO>> getall() {
        List<DoctorDTO> doctors = doctorservices.getalldoctor()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @PutMapping("/update")
    ResponseEntity<DoctorDTO> updatedoctor(@RequestBody DoctorDTO data) {
        Doctor doctor = convertToEntity(data);
        Doctor updatedDoctor = doctorservices.updatedoctor(doctor);

        return new ResponseEntity<>(
                convertToDTO(updatedDoctor),
                HttpStatus.ACCEPTED
        );
    }

    @GetMapping("/getbyid/{id}")
    ResponseEntity<?> getbyId(@PathVariable long id) {
        try {
            Doctor response = doctorservices.getbyid(id);

            return new ResponseEntity<>(
                    convertToDTO(response),
                    HttpStatus.OK
            );

        } catch (RuntimeException exception) {
            return new ResponseEntity<>("not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> deletedoctor(@PathVariable long id) {
        doctorservices.deletedoctor(id);

        return new ResponseEntity<>(
                "Doctor deleted successfully",
                HttpStatus.OK
        );
    }
}