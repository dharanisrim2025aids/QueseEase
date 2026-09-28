package Dharanisri.Project.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Token {

    @Id
    @GeneratedValue
    Long Id;

    int TokenNumber;
    LocalDate TokenDate;
    boolean IsPriority;
    String Status;
    int EstimatedWaitTime;

    @ManyToOne
    Doctor doctor;

    @ManyToOne
    Patient patient;
}
