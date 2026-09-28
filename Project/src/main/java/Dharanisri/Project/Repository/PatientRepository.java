package Dharanisri.Project.Repository;

import Dharanisri.Project.Models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}