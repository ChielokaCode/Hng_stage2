package com.chielokacodes.userorgapp.services;

import com.chielokacodes.userorgapp.dto.LoginRequest;
import com.chielokacodes.userorgapp.dto.SignupRequest;
import com.chielokacodes.userorgapp.exeption.ErrorResponse;
import com.chielokacodes.userorgapp.exeption.SuccessResponse;

public interface UserService {
    Object saveUser(SignupRequest signupRequest) throws Throwable;
    SuccessResponse logInUser(LoginRequest loginRequest) throws ErrorResponse;
    SuccessResponse getUser(Long id);
}
