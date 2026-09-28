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

    public Doctor createdoctor(Doctor data) {
        return doctorrepository.save(data);
    }

    public List<Doctor> getalldoctor() {
        return doctorrepository.findAll();
    }

    public Doctor updatedoctor(Doctor data) {
        if (data.getId() == null ||
                !doctorrepository.existsById(data.getId())) {
            throw new RuntimeException("Doctor not found");
        }

        return doctorrepository.save(data);
    }

    public Doctor getbyid(Long id) {
        return doctorrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public void deletedoctor(Long id) {
        if (!doctorrepository.existsById(id)) {
            throw new RuntimeException("Doctor not found");
        }

        doctorrepository.deleteById(id);
    }
}