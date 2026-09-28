package Dharanisri.Project.Controller;

import Dharanisri.Project.Models.Patient;
import Dharanisri.Project.Services.PatientServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientServices patientservices;

    @PostMapping("/create")
    ResponseEntity<Patient> createpatient(@RequestBody Patient body){
        return new ResponseEntity<>(patientservices.createpatient(body), HttpStatus.CREATED);
    }

    @GetMapping("/getall")
    ResponseEntity<List<Patient>> getall(){
        return new ResponseEntity<>(patientservices.getallpatient(), HttpStatus.OK);
    }

    @PutMapping("/update")
    ResponseEntity<Patient> updatepatient(@RequestBody Patient data){
        return new ResponseEntity<>(patientservices.updatepatient(data), HttpStatus.ACCEPTED);
    }

    @GetMapping("getbyid/{id}")
    ResponseEntity<?> getbyId(@PathVariable long id){
        try{
            Patient response = patientservices.getbyid(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException exception){
            return new ResponseEntity<>("not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> deletepatient(@PathVariable long id){
        patientservices.deletepatient(id);
        return new ResponseEntity<>("Patient deleted successfully", HttpStatus.OK);
    }
}
