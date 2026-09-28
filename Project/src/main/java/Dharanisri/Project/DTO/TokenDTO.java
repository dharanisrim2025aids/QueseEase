package Dharanisri.Project.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TokenDTO {

    Long Id;
    int TokenNumber;
    LocalDate TokenDate;
    boolean IsPriority;
    String Status;
    int EstimatedWaitTime;
    Long doctorId;
    Long patientId;
}
