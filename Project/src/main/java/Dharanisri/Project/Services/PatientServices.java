package Dharanisri.Project.Services;

import Dharanisri.Project.Models.Patient;
import Dharanisri.Project.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServices {

    @Autowired
    private PatientRepository patientrepository;

    public Patient createpatient(Patient data) {
        return patientrepository.save(data);
    }

    public List<Patient> getallpatient() {
        return patientrepository.findAll();
    }

    public Patient updatepatient(Patient data) {
        if (data.getId() == null ||
                !patientrepository.existsById(data.getId())) {
            throw new RuntimeException("Patient not found");
        }

        return patientrepository.save(data);
    }

    public Patient getbyid(Long id) {
        return patientrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    public void deletepatient(Long id) {
        if (!patientrepository.existsById(id)) {
            throw new RuntimeException("Patient not found");
        }

        patientrepository.deleteById(id);
    }
}