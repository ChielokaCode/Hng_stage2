package com.chielokacodes.userorgapp.serviceImpl;

import com.chielokacodes.userorgapp.dto.DataResponse;
import com.chielokacodes.userorgapp.dto.SignupRequest;
import com.chielokacodes.userorgapp.dto.SignupResponse;
import com.chielokacodes.userorgapp.exeption.SuccessResponse;
import com.chielokacodes.userorgapp.exeption.validation.Error;
import com.chielokacodes.userorgapp.exeption.validation.Errors;
import com.chielokacodes.userorgapp.model.Organisation;
import com.chielokacodes.userorgapp.model.User;
import com.chielokacodes.userorgapp.model.UserValidator;
import com.chielokacodes.userorgapp.repository.OrganisationRepository;
import com.chielokacodes.userorgapp.repository.UserRepository;
import com.chielokacodes.userorgapp.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganisationRepository organisationRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private OrganisationServiceImpl organisationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtils.createJwt = mock(Function.class);

    }

    @Test
    void testSaveUserWithInvalidValidation() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setEmail("john.doe@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setPhone("12345678901");

        Errors validationErrors = new Errors();
        validationErrors.setErrors(List.of(new Error("email", "Email already exists")));

        when(userValidator.validateUser(signupRequest)).thenReturn(validationErrors);

        Object response = userService.saveUser(signupRequest);
        assertTrue(response instanceof Errors);

        Errors errors = (Errors) response;
        assertEquals(1, errors.getErrors().size());
        assertEquals("email", errors.getErrors().get(0).getField());
        assertEquals("Email already exists", errors.getErrors().get(0).getMessage());

        // Ensure userRepository.save() was not called
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSaveUserWithValidData() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setEmail("john.doe@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setPhone("12345678901");

        when(userValidator.validateUser(signupRequest)).thenReturn(null);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(new User()));
        when(jwtUtils.createJwt.apply(any(User.class))).thenReturn("dummyToken");
        when(organisationRepository.save(any(Organisation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Object response = userService.saveUser(signupRequest);
        assertTrue(response instanceof SuccessResponse);

        SuccessResponse successResponse = (SuccessResponse) response;
        assertEquals("success", successResponse.getStatus());
        assertEquals("Registration Successful", successResponse.getMessage());

        DataResponse data = successResponse.getData();
        assertNotNull(data);
        assertEquals("dummyToken", data.getAccessToken());
    }


    @Test
    void testSaveUserWithInvalidPhoneFormat() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setEmail("john.doe@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setPhone("invalidPhone");

        Errors validationErrors = new Errors();
        validationErrors.setErrors(List.of(new Error("phone", "Phone number must be numbers only")));

        when(userValidator.validateUser(signupRequest)).thenReturn(validationErrors);

        Object response = userService.saveUser(signupRequest);
        assertTrue(response instanceof Errors);

        Errors errors = (Errors) response;
        assertEquals(1, errors.getErrors().size());
        assertEquals("phone", errors.getErrors().get(0).getField());
        assertEquals("Phone number must be numbers only", errors.getErrors().get(0).getMessage());

        // Ensure userRepository.save() was not called
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSaveUserWithEmptyFields() {
        SignupRequest signupRequest = new SignupRequest();

        Errors validationErrors = new Errors();
        validationErrors.setErrors(List.of(
                new Error("firstName", "First name must not be null or empty"),
                new Error("lastName", "Last name must not be null or empty"),
                new Error("email", "Email must not be null or empty"),
                new Error("password", "Password must not be null or empty"),
                new Error("phone", "Phone must not be null or empty")
        ));

        when(userValidator.validateUser(signupRequest)).thenReturn(validationErrors);

        Object response = userService.saveUser(signupRequest);
        assertTrue(response instanceof Errors);

        Errors errors = (Errors) response;
        assertEquals(5, errors.getErrors().size());

        // Ensure userRepository.save() was not called
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSaveUserWithNullValidationResponse() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setEmail("john.doe@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setPhone("12345678901");

        when(userValidator.validateUser(signupRequest)).thenReturn(null);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1L); // Set a dummy ID
            return user;
        });
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(new User()));
        when(jwtUtils.createJwt.apply(any(User.class))).thenReturn("dummyToken");
        when(organisationRepository.save(any(Organisation.class))).thenAnswer(invocation -> {
            Organisation org = invocation.getArgument(0);
            org.setOrgId(1L); // Set a dummy ID for the organisation
            return org;
        });

        Object response = userService.saveUser(signupRequest);

        assertTrue(response instanceof SuccessResponse);

        SuccessResponse successResponse = (SuccessResponse) response;
        assertEquals("success", successResponse.getStatus());
        assertEquals("Registration Successful", successResponse.getMessage());

        DataResponse dataResponse = successResponse.getData();
        assertNotNull(dataResponse);
        assertEquals("dummyToken", dataResponse.getAccessToken());

        SignupResponse signupResponse = dataResponse.getUser();
        assertNotNull(signupResponse);
        assertEquals(1L, signupResponse.getUserId());
        assertEquals("John", signupResponse.getFirstName());
        assertEquals("Doe", signupResponse.getLastName());
        assertEquals("john.doe@example.com", signupResponse.getEmail());
        assertEquals("encodedPassword", signupResponse.getPassword());
        assertEquals("12345678901", signupResponse.getPhone());
    }



}
