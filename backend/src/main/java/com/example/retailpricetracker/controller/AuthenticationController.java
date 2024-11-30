package com.example.retailpricetracker.controller;

import com.example.retailpricetracker.entity.User;
import com.example.retailpricetracker.responses.LoginResponse;
import com.example.retailpricetracker.service.AuthenticationService;
import com.example.retailpricetracker.service.JwtService;
import com.example.retailpricetracker.dtos.LoginUserDto;
import com.example.retailpricetracker.dtos.RegisterUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@CrossOrigin("http://localhost:3000")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public  ResponseEntity<LoginResponse>  register(@RequestBody RegisterUserDto registerUserDto) {
        // Call the modified signup method
        User registeredUser = authenticationService.signup(registerUserDto);

        // Generate token for the registered user
        String jwtToken = jwtService.generateToken(registeredUser);

        // Prepare response with token and expiration time
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse); // Return token along with the user
    }
    // Token will be generated after login (email and password correctly given)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        // Log session details
        System.out.println("User " + authenticatedUser.getUsername() + " logged in successfully. JWT token expires in " +  jwtService.getExpirationTime() + " milliseconds.");

        return ResponseEntity.ok(loginResponse);
    }
}
