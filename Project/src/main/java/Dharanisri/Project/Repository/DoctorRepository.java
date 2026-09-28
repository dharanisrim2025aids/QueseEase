package Dharanisri.Project.Repository;

import Dharanisri.Project.Models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}