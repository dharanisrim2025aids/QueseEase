package Dharanisri.Project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequestDTO {

    private Long id;
    private String doctorName;
    private String specialization;
    private int averageConsultationTime;
    private int currentServingToken;
}