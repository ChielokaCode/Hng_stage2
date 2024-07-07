package com.chielokacodes.userorgapp.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> UsernameNotFoundException(
            UsernameNotFoundException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailIsTakenException.class)
    public ResponseEntity<ApiError> EmailIsTakenException(
            EmailIsTakenException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<ApiError> UserNotVerifiedException(
            UserNotVerifiedException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApiError> buildErrorResponse(String message, HttpStatus status) {
        ApiError apiError = new ApiError(
                status.getReasonPhrase(),
                message,
                status.value()
        );
        return new ResponseEntity<>(apiError, status);
    }

//    @ExceptionHandler(ErrorResponse.class)
//    public ResponseEntity<ErrorResponse> handleCustomError(ErrorResponse ex) {
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setStatus(ex.getStatus());
//        errorResponse.setMessage(ex.getMessage());
//        errorResponse.setStatusCode(ex.getStatusCode());
//        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
//    }
}
