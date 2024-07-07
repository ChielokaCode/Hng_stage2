package com.chielokacodes.userorgapp.controller;

import com.chielokacodes.userorgapp.dto.LoginRequest;
import com.chielokacodes.userorgapp.dto.SignupRequest;
import com.chielokacodes.userorgapp.exeption.SuccessResponse;
import com.chielokacodes.userorgapp.exeption.validation.Errors;
import com.chielokacodes.userorgapp.serviceImpl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> signUpUser(@RequestBody SignupRequest signupRequest, final HttpServletRequest request) {
        Object response = userService.saveUser(signupRequest);

        if (response instanceof Errors) {
            // Handle validation errors
            return ResponseEntity.badRequest().body(((Errors) response).getErrors());
        } else if (response instanceof SuccessResponse) {
            // Return success response
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            // Handle unexpected case, though ideally shouldn't occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected response type");
        }
    }


    @PostMapping("/auth/login")
    public ResponseEntity<SuccessResponse> login(@RequestBody LoginRequest loginRequest) {
        SuccessResponse successResponse = userService.logInUser(loginRequest);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @GetMapping("/api/users/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SuccessResponse> getUsers(@PathVariable Long id){
        SuccessResponse successResponse = userService.getUser(id);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }


}
