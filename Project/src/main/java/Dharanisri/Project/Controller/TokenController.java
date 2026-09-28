
package Dharanisri.Project.Controller;

import Dharanisri.Project.Models.Token;
import Dharanisri.Project.Services.TokenServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Autowired
    private TokenServices tokenservices;

    @PostMapping("/create")
    public ResponseEntity<Token> createtoken(@RequestBody Token data) {
        Token result = tokenservices.createtoken(data);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Token>> getalltoken() {
        List<Token> result = tokenservices.getalltoken();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Token> updatetoken(@RequestBody Token data) {
        Token result = tokenservices.updatetoken(data);
        return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<Token> getbyid(@PathVariable Long id) {
        Token result = tokenservices.getbyid(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletetoken(@PathVariable Long id) {
        tokenservices.deletetoken(id);
        return new ResponseEntity<>("Token deleted successfully", HttpStatus.OK);
    }

    // Automatic Token Generation
    @PostMapping("/generate")
    public ResponseEntity<Token> generatetoken(
            @RequestParam Long doctorId,
            @RequestParam Long patientId) {

        Token result = tokenservices.generatetoken(doctorId, patientId);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // Advance Current Serving Token
    @PutMapping("/advance/{doctorId}")
    public ResponseEntity<Token> advanceToken(
            @PathVariable Long doctorId) {

        Token result = tokenservices.advanceToken(doctorId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Emergency / Priority Token Generation
    @PostMapping("/priority")
    public ResponseEntity<Token> generatePriorityToken(
            @RequestParam Long doctorId,
            @RequestParam Long patientId) {

        Token result =
                tokenservices.generatePriorityToken(doctorId, patientId);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // Doctor-wise Daily Token History
    @GetMapping("/history/{doctorId}")
    public ResponseEntity<List<Token>> getDailyHistory(
            @PathVariable Long doctorId) {

        List<Token> result =
                tokenservices.getDailyHistory(doctorId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}