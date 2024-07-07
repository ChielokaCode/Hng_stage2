package com.chielokacodes.userorgapp.model;

import com.chielokacodes.userorgapp.dto.SignupRequest;
import com.chielokacodes.userorgapp.exeption.validation.Error;
import com.chielokacodes.userorgapp.exeption.validation.Errors;
import com.chielokacodes.userorgapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class UserValidator {
    private final UserRepository userRepository;

    @Autowired
    public UserValidator(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Errors validateUser(SignupRequest signupRequest) {
        Errors errors = new Errors();
        List<Error> errorList = new ArrayList<>();

        // Validate firstName
        String firstName = signupRequest.getFirstName();
        if (firstName == null || firstName.isEmpty()) {
            errorList.add(new Error("firstName", "First name must not be null or empty"));
        }

        // Validate lastName
        String lastName = signupRequest.getLastName();
        if (lastName == null || lastName.isEmpty()) {
            errorList.add(new Error("lastName", "Last name must not be null or empty"));
        }

        // Validate email
        String email = signupRequest.getEmail();
        if (email == null || email.isEmpty()) {
            errorList.add(new Error("email", "Email must not be null or empty"));
        } else if (userRepository.existsByEmail(email)) {
            errorList.add(new Error("email", "Email already exists"));
        } else if(!isValidEmail(email)){
            errorList.add(new Error("email", "Email is in an incorrect format"));
        }

        // Validate password
        String password = signupRequest.getPassword();
        if (password == null || password.isEmpty()) {
            errorList.add(new Error("password", "Password must not be null or empty"));
        } else if (!password.isEmpty() && password.length() <= 3){
            errorList.add(new Error("password", "Password should be more than 3 characters"));
        }

        // Validate phone (optional field, no validation required)
        String phone = signupRequest.getPhone();
        if (phone == null || phone.isEmpty()) {
            errorList.add(new Error("phone", "Phone must not be null or empty"));
        } else if (phone.length() < 11){
            errorList.add(new Error("phone", "Phone number must be eleven digits"));
        }
        try {
            Long.parseLong(phone); // Try to parse the phone number as a Long
        } catch (NumberFormatException e) {
            // If parsing fails, add an error indicating phone number is not digits
            errorList.add(new Error("phone", "Phone number must be numbers only"));
        }


        if (!errorList.isEmpty()) {
            errors.setErrors(errorList);
            return errors;
        }

        // If no errors, return null
        return null;
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
