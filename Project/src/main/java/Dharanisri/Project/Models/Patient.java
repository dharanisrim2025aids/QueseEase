package Dharanisri.Project.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Patient {

    @Id
    @GeneratedValue
    Long Id;

    String PatientName;
    int Age;
    String Gender;
    String ContactNumber;
}