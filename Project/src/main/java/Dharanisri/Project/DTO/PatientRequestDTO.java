package Dharanisri.Project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDTO {

    private Long id;
    private String patientName;
    private int age;
    private String gender;
    private String contactNumber;
}