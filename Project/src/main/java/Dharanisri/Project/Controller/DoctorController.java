package Dharanisri.Project.Controller;

import Dharanisri.Project.Models.Doctor;
import Dharanisri.Project.Services.DoctorServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorServices doctorservices;

    @PostMapping("/create")
    ResponseEntity<Doctor> createdoctor(@RequestBody Doctor body){
        return new ResponseEntity<>(doctorservices.createdoctor(body), HttpStatus.CREATED);
    }

    @GetMapping("/getall")
    ResponseEntity<List<Doctor>> getall(){
        return new ResponseEntity<>(doctorservices.getalldoctor(), HttpStatus.OK);
    }

    @PutMapping("/update")
    ResponseEntity<Doctor> updatedoctor(@RequestBody Doctor data){
        return new ResponseEntity<>(doctorservices.updatedoctor(data), HttpStatus.ACCEPTED);
    }

    @GetMapping("getbyid/{id}")
    ResponseEntity<?> getbyId(@PathVariable long id){
        try{
            Doctor response = doctorservices.getbyid(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException exception){
            return new ResponseEntity<>("not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> deletedoctor(@PathVariable long id){
        doctorservices.deletedoctor(id);
        return new ResponseEntity<>("Doctor deleted successfully", HttpStatus.OK);
    }
}