package Dharanisri.Project.Services;

import Dharanisri.Project.Models.Doctor;
import Dharanisri.Project.Repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServices {

    @Autowired
    private DoctorRepository doctorrepository;

    public Doctor createdoctor(Doctor data){
        Doctor result = doctorrepository.save(data);
        return result;
    }

    public List<Doctor> getalldoctor(){
        return doctorrepository.findAll();
    }

    public Doctor updatedoctor(Doctor data){
        return doctorrepository.save(data);
    }

    public Doctor getbyid(Long Id){
        return doctorrepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public void deletedoctor(Long Id){
        doctorrepository.deleteById(Id);
    }
}