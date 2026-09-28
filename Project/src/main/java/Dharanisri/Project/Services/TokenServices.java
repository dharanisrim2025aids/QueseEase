package Dharanisri.Project.Services;

import Dharanisri.Project.Models.Doctor;
import Dharanisri.Project.Models.Patient;
import Dharanisri.Project.Models.Token;
import Dharanisri.Project.Repository.DoctorRepository;
import Dharanisri.Project.Repository.PatientRepository;
import Dharanisri.Project.Repository.TokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TokenServices {

    @Autowired
    private TokenRepository tokenrepository;

    @Autowired
    private PatientRepository patientrepository;

    @Autowired
    private DoctorRepository doctorrepository;

    public List<Token> getalltoken() {
        return tokenrepository.findAll();
    }

    public Token getbyid(Long id) {
        return tokenrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Token not found"));
    }

    public Token generatetoken(Long doctorId, Long patientId) {

        Doctor doctor = doctorrepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientrepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LocalDate today = LocalDate.now();

        List<Token> existingTokens =
                tokenrepository.findByDoctorIdAndTokenDate(doctorId, today);

        int nextTokenNumber = existingTokens.stream()
                .mapToInt(Token::getTokenNumber)
                .max()
                .orElse(0) + 1;

        long waitingCount = existingTokens.stream()
                .filter(token -> "WAITING".equalsIgnoreCase(token.getStatus()))
                .count();

        Token token = new Token();

        token.setTokenNumber(nextTokenNumber);
        token.setTokenDate(today);
        token.setPriority(false);
        token.setStatus("WAITING");
        token.setEstimatedWaitTime(
                (int) waitingCount * doctor.getAverageConsultationTime()
        );
        token.setPatient(patient);
        token.setDoctor(doctor);

        return tokenrepository.save(token);
    }

    public Token updatetoken(Long id, Token data) {

        Token existing = getbyid(id);

        existing.setStatus(data.getStatus());
        existing.setPriority(data.isPriority());
        existing.setEstimatedWaitTime(data.getEstimatedWaitTime());

        return tokenrepository.save(existing);
    }

    public Token makepriority(Long doctorId, Long patientId) {

        List<Token> allTokens = tokenrepository.findAll();

        Token token = allTokens.stream()
                .filter(item -> item.getDoctor().getId().equals(doctorId))
                .filter(item -> item.getPatient().getId().equals(patientId))
                .filter(item -> item.getTokenDate().equals(LocalDate.now()))
                .filter(item -> "WAITING".equalsIgnoreCase(item.getStatus()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Waiting token not found"));

        token.setPriority(true);

        return tokenrepository.save(token);
    }

    public void deletetoken(Long id) {

        if (!tokenrepository.existsById(id)) {
            throw new RuntimeException("Token not found");
        }

        tokenrepository.deleteById(id);
    }
}