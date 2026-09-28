package Dharanisri.Project.Repository;

import Dharanisri.Project.Models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByDoctorIdAndTokenDate(Long doctorId, LocalDate tokenDate);
}