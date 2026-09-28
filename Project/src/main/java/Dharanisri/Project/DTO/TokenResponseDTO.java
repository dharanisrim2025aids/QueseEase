package Dharanisri.Project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {

    private Long id;
    private int tokenNumber;
    private LocalDate tokenDate;
    private boolean isPriority;
    private String status;
    private int estimatedWaitTime;
    private PatientResponseDTO patient;
    private DoctorResponseDTO doctor;
}
