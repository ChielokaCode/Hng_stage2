package com.chielokacodes.userorgapp.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotVerifiedException extends RuntimeException {
    public UserNotVerifiedException(String message){
        super(message);
    }
}
