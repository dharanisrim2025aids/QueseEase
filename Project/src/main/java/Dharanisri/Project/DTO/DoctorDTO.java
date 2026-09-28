package Dharanisri.Project.DTO;

import lombok.Data;

@Data
public class DoctorDTO {

    Long Id;
    String DoctorName;
    String Specialization;
    int AverageConsultationTime;
    int CurrentServingToken;
}
