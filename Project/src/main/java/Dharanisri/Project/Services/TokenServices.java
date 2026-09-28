
package Dharanisri.Project.Services;

import Dharanisri.Project.Models.Token;
import Dharanisri.Project.Models.Doctor;
import Dharanisri.Project.Models.Patient;
import Dharanisri.Project.Repository.TokenRepository;
import Dharanisri.Project.Repository.DoctorRepository;
import Dharanisri.Project.Repository.PatientRepository;

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

    public Token createtoken(Token data) {
        Token result = tokenrepository.save(data);
        return result;
    }

    public List<Token> getalltoken() {
        return tokenrepository.findAll();
    }

    public Token updatetoken(Token data) {
        return tokenrepository.save(data);
    }

    public Token getbyid(Long Id) {
        return tokenrepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Token not found"));
    }

    public void deletetoken(Long Id) {
        tokenrepository.deleteById(Id);
    }

    // Automatic Token Generation
    public Token generatetoken(Long doctorId, Long patientId) {

        Doctor doctor = doctorrepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientrepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LocalDate today = LocalDate.now();

        List<Token> todayTokens =
                tokenrepository.findByDoctorAndDate(doctorId, today);

        int nextTokenNumber = 1;

        for (Token token : todayTokens) {
            if (token.getTokenNumber() >= nextTokenNumber) {
                nextTokenNumber = token.getTokenNumber() + 1;
            }
        }

        Token newToken = new Token();

        newToken.setTokenNumber(nextTokenNumber);
        newToken.setTokenDate(today);
        newToken.setIsPriority(false);
        newToken.setStatus("WAITING");
        newToken.setEstimatedWaitTime(0);
        newToken.setDoctor(doctor);
        newToken.setPatient(patient);

        tokenrepository.save(newToken);

        updateWaitingTimes(doctorId, today);

        return tokenrepository.save(newToken);
    }

    // Priority Token Generation
    public Token generatePriorityToken(Long doctorId, Long patientId) {

        Doctor doctor = doctorrepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientrepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LocalDate today = LocalDate.now();

        boolean priorityExists =
                tokenrepository.existsActivePriorityToken(doctorId, today);

        if (priorityExists) {
            throw new RuntimeException(
                    "An active priority token already exists for this doctor"
            );
        }

        List<Token> todayTokens =
                tokenrepository.findByDoctorAndDate(doctorId, today);

        int nextTokenNumber = 1;

        for (Token token : todayTokens) {
            if (token.getTokenNumber() >= nextTokenNumber) {
                nextTokenNumber = token.getTokenNumber() + 1;
            }
        }

        Token priorityToken = new Token();

        priorityToken.setTokenNumber(nextTokenNumber);
        priorityToken.setTokenDate(today);
        priorityToken.setIsPriority(true);
        priorityToken.setStatus("WAITING");
        priorityToken.setEstimatedWaitTime(0);
        priorityToken.setDoctor(doctor);
        priorityToken.setPatient(patient);

        tokenrepository.save(priorityToken);

        updateWaitingTimes(doctorId, today);

        return tokenrepository.save(priorityToken);
    }

    // Advance Current Serving Token
    public Token advanceToken(Long doctorId) {

        Doctor doctor = doctorrepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDate today = LocalDate.now();

        List<Token> todayTokens =
                tokenrepository.findByDoctorAndDate(doctorId, today);

        Token nextToken = null;

        for (Token token : todayTokens) {

            if (token.getStatus().equals("SERVING")) {
                token.setStatus("COMPLETED");
                tokenrepository.save(token);
            }

            if (token.getStatus().equals("WAITING")) {

                if (nextToken == null) {
                    nextToken = token;
                } else if (token.isIsPriority() && !nextToken.isIsPriority()) {
                    nextToken = token;
                } else if (token.isIsPriority() == nextToken.isIsPriority()
                        && token.getTokenNumber() < nextToken.getTokenNumber()) {
                    nextToken = token;
                }
            }
        }

        if (nextToken == null) {
            throw new RuntimeException("No waiting tokens available");
        }

        nextToken.setStatus("SERVING");

        doctor.setCurrentServingToken(nextToken.getTokenNumber());
        doctorrepository.save(doctor);

        tokenrepository.save(nextToken);

        updateWaitingTimes(doctorId, today);

        return nextToken;
    }

    // Doctor-wise Daily Token History
    public List<Token> getDailyHistory(Long doctorId) {

        doctorrepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

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

        for (Token token : tokens) {
            if (token.getStatus().equals("SERVING")) {
                patientsAhead++;
            }
        }

        List<Token> waitingTokens = tokens.stream()
                .filter(token -> token.getStatus().equals("WAITING"))
                .sorted(Comparator
                        .comparing(Token::isIsPriority).reversed()
                        .thenComparing(Token::getTokenNumber))
                .toList();

        for (Token token : waitingTokens) {

            token.setEstimatedWaitTime(
                    patientsAhead * consultationTime
            );

            tokenrepository.save(token);

            patientsAhead++;
        }
    }
}