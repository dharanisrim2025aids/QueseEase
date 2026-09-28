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

    public Patient createpatient(Patient data){
        Patient result = patientrepository.save(data);
        return result;
    }

    public List<Patient> getallpatient(){
        return patientrepository.findAll();
    }

    public Patient updatepatient(Patient data){
        return patientrepository.save(data);
    }

    public Patient getbyid(Long Id){
        return patientrepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    public void deletepatient(Long Id){
        patientrepository.deleteById(Id);
    }
}