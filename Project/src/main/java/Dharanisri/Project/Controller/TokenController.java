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

    // Create Token
    @PostMapping("/create")
    public ResponseEntity<Token> createToken(@RequestBody Token token) {

        Token result = tokenservices.createToken(token);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // Get All Tokens
    @GetMapping("/getall")
    public ResponseEntity<List<Token>> getAllTokens() {

        List<Token> result = tokenservices.getAllTokens();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Get Token By ID
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<Token> getTokenById(@PathVariable Long id) {

        Token result = tokenservices.getTokenById(id);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Update Token
    @PutMapping("/update/{id}")
    public ResponseEntity<Token> updateToken(
            @PathVariable Long id,
            @RequestBody Token token) {

        Token result = tokenservices.updateToken(id, token);

        return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
    }

    // Delete Token
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteToken(@PathVariable Long id) {

        tokenservices.deleteToken(id);

        return new ResponseEntity<>(
                "Token deleted successfully",
                HttpStatus.OK
        );
    }

    // Generate Normal Token
    @PostMapping("/generate")
    public ResponseEntity<Token> generateToken(
            @RequestParam Long doctorId,
            @RequestParam Long patientId) {

        Token result = tokenservices.generatetoken(doctorId, patientId);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // Generate Priority Token
    @PostMapping("/priority")
    public ResponseEntity<Token> generatePriorityToken(
            @RequestParam Long doctorId,
            @RequestParam Long patientId) {

        Token result = tokenservices.generatePriorityToken(
                doctorId, patientId
        );

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // Advance Serving Token
    @PutMapping("/advance/{doctorId}")
    public ResponseEntity<Token> advanceToken(
            @PathVariable Long doctorId) {

        Token result = tokenservices.advanceToken(doctorId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Get Daily Token History
    @GetMapping("/history/{doctorId}")
    public ResponseEntity<List<Token>> getDailyHistory(
            @PathVariable Long doctorId) {

        List<Token> result = tokenservices.getDailyHistory(doctorId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}