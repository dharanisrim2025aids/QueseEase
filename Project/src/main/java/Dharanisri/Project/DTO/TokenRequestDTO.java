package Dharanisri.Project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequestDTO {

    private Long id;
    private int tokenNumber;
    private boolean priority;
    private String status;
    private int estimatedWaitTime;
    private Long patientId;
    private Long doctorId;
}