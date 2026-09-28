package Dharanisri.Project.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Doctor {

    @Id
    @GeneratedValue
    Long Id;

    String DoctorName;
    String Specialization;
    int AverageConsultationTime;
    int CurrentServingToken;
}