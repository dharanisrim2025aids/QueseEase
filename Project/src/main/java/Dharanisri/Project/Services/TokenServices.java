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
import java.util.Comparator;
import java.util.List;

@Service
public class TokenServices {

    @Autowired
    private TokenRepository tokenrepository;

    @Autowired
    private DoctorRepository doctorrepository;

    @Autowired
    private PatientRepository patientrepository;


    // Create Token
    public Token createToken(Token token) {

        Long doctorId = token.getDoctor().getId();
        Long patientId = token.getPatient().getId();

        Doctor doctor = doctorrepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientrepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        token.setDoctor(doctor);
        token.setPatient(patient);

        return tokenrepository.save(token);
    }


    // Get All Tokens
    public List<Token> getAllTokens() {
        return tokenrepository.findAll();
    }


    // Get Token By ID
    public Token getTokenById(Long id) {
        return tokenrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Token not found"));
    }


    // Update Token
    public Token updateToken(Long id, Token updatedToken) {

        Token token = tokenrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        token.setTokenNumber(updatedToken.getTokenNumber());
        token.setTokenDate(updatedToken.getTokenDate());
        token.setIsPriority(updatedToken.isIsPriority());
        token.setStatus(updatedToken.getStatus());
        token.setEstimatedWaitTime(updatedToken.getEstimatedWaitTime());
        token.setDoctor(updatedToken.getDoctor());
        token.setPatient(updatedToken.getPatient());

        return tokenrepository.save(token);
    }


    // Delete Token
    public void deleteToken(Long id) {

        Token token = tokenrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        tokenrepository.delete(token);
    }


    // Generate Normal Token
    public Token generatetoken(Long doctorId, Long patientId) {

        Doctor doctor = doctorrepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientrepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LocalDate today = LocalDate.now();

        List<Token> tokens =
                tokenrepository.findByDoctorAndDate(doctorId, today);

        int nextTokenNumber = tokens.stream()
                .mapToInt(Token::getTokenNumber)
                .max()
                .orElse(0) + 1;

        Token token = new Token();

        token.setTokenNumber(nextTokenNumber);
        token.setTokenDate(today);
        token.setIsPriority(false);
        token.setStatus("WAITING");
        token.setEstimatedWaitTime(0);
        token.setDoctor(doctor);
        token.setPatient(patient);

        Token savedToken = tokenrepository.save(token);

        updateWaitingTimes(doctorId, today);

        return savedToken;
    }


    // Generate Priority Token
    public Token generatePriorityToken(Long doctorId, Long patientId) {

        Doctor doctor = doctorrepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientrepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LocalDate today = LocalDate.now();

        List<Token> tokens =
                tokenrepository.findByDoctorAndDate(doctorId, today);

        boolean activePriorityExists = tokens.stream()
                .anyMatch(token ->
                        token.isIsPriority()
                                && (token.getStatus().equals("WAITING")
                                || token.getStatus().equals("SERVING"))
                );

        if (activePriorityExists) {
            throw new RuntimeException(
                    "An active priority token already exists for this doctor"
            );
        }

        int nextTokenNumber = tokens.stream()
                .mapToInt(Token::getTokenNumber)
                .max()
                .orElse(0) + 1;

        Token token = new Token();

        token.setTokenNumber(nextTokenNumber);
        token.setTokenDate(today);
        token.setIsPriority(true);
        token.setStatus("WAITING");
        token.setEstimatedWaitTime(0);
        token.setDoctor(doctor);
        token.setPatient(patient);

        Token savedToken = tokenrepository.save(token);

        updateWaitingTimes(doctorId, today);

        return savedToken;
    }


    // Advance Serving Token
    public Token advanceToken(Long doctorId) {

        Doctor doctor = doctorrepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDate today = LocalDate.now();

        List<Token> tokens =
                tokenrepository.findByDoctorAndDate(doctorId, today);

        // Complete the current serving token
        tokens.stream()
                .filter(token -> token.getStatus().equals("SERVING"))
                .findFirst()
                .ifPresent(token -> {
                    token.setStatus("COMPLETED");
                    token.setEstimatedWaitTime(0);
                    tokenrepository.save(token);
                });

        // Select next token: Priority first, then token number
        Token nextToken = tokens.stream()
                .filter(token -> token.getStatus().equals("WAITING"))
                .sorted(Comparator
                        .comparing(Token::isIsPriority).reversed()
                        .thenComparing(Token::getTokenNumber))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No waiting tokens available")
                );

        nextToken.setStatus("SERVING");
        nextToken.setEstimatedWaitTime(0);

        doctor.setCurrentServingToken(nextToken.getTokenNumber());
        doctorrepository.save(doctor);

        Token savedToken = tokenrepository.save(nextToken);

        updateWaitingTimes(doctorId, today);

        return savedToken;
    }


    // Get Daily Token History
    public List<Token> getDailyHistory(Long doctorId) {

        LocalDate today = LocalDate.now();

        return tokenrepository.findByDoctorAndDate(doctorId, today);
    }


    // Update Estimated Waiting Time
    private void updateWaitingTimes(Long doctorId, LocalDate today) {

        Doctor doctor = doctorrepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<Token> tokens =
                tokenrepository.findByDoctorAndDate(doctorId, today);

        int consultationTime = doctor.getAverageConsultationTime();

        int patientsAhead = 0;

        // Reset waiting time for completed and serving tokens
        for (Token token : tokens) {

            if (token.getStatus().equals("SERVING")) {
                token.setEstimatedWaitTime(0);
                patientsAhead++;
                tokenrepository.save(token);
            }

            else if (token.getStatus().equals("COMPLETED")) {
                token.setEstimatedWaitTime(0);
                tokenrepository.save(token);
            }
        }

        // Sort waiting tokens: Priority first, then token number
        List<Token> waitingTokens = tokens.stream()
                .filter(token -> token.getStatus().equals("WAITING"))
                .sorted(Comparator
                        .comparing(Token::isIsPriority).reversed()
                        .thenComparing(Token::getTokenNumber))
                .toList();

        // Calculate estimated waiting time
        for (Token token : waitingTokens) {

            token.setEstimatedWaitTime(
                    patientsAhead * consultationTime
            );

            tokenrepository.save(token);

            patientsAhead++;
        }
    }
}