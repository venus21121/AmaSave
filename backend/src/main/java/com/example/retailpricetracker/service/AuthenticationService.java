package com.example.retailpricetracker.service;

import com.example.retailpricetracker.dtos.LoginUserDto;
import com.example.retailpricetracker.dtos.RegisterUserDto;
import com.example.retailpricetracker.entity.User;
import com.example.retailpricetracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {
        // Check if the email already exists in the database
        if (userRepository.findByUserEmail(input.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This email is already registered.");
        }
        User user = new User();
        user.setUserEmail(input.getEmail()); // Correctly setting the email
        user.setPassword(passwordEncoder.encode(input.getPassword())); // Correctly setting the password

        User registeredUser = userRepository.save(user); // Save the user to the database

        // Automatically authenticate the user after registration
        // You may need to create a LoginUserDto object for this
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail(input.getEmail());
        loginUserDto.setPassword(input.getPassword());

        // Call authenticate to get the token
        User authenticatedUser = authenticate(loginUserDto); // Re-use the authenticate logic

        return authenticatedUser; // Return the authenticated user if needed
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByUserEmail(input.getEmail())
                .orElseThrow();
    }
}
