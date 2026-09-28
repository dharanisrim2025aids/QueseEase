
package Dharanisri.Project.Repository;

import Dharanisri.Project.Models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t FROM Token t WHERE t.doctor.Id = :doctorId AND t.TokenDate = :tokenDate")
    List<Token> findByDoctorAndDate(
            @Param("doctorId") Long doctorId,
            @Param("tokenDate") LocalDate tokenDate
    );

    @Query("SELECT COUNT(t) > 0 FROM Token t WHERE t.doctor.Id = :doctorId AND t.TokenDate = :tokenDate AND t.IsPriority = true AND t.Status IN ('WAITING', 'SERVING')")
    boolean existsActivePriorityToken(
            @Param("doctorId") Long doctorId,
            @Param("tokenDate") LocalDate tokenDate
    );
}