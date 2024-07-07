package com.chielokacodes.userorgapp.serviceImpl;

import com.chielokacodes.userorgapp.dto.DataResponse;
import com.chielokacodes.userorgapp.dto.LoginRequest;
import com.chielokacodes.userorgapp.dto.SignupRequest;
import com.chielokacodes.userorgapp.dto.SignupResponse;
import com.chielokacodes.userorgapp.enums.Role;
import com.chielokacodes.userorgapp.exeption.ErrorResponse;
import com.chielokacodes.userorgapp.exeption.SuccessResponse;
import com.chielokacodes.userorgapp.exeption.UserNotVerifiedException;
import com.chielokacodes.userorgapp.exeption.validation.Errors;
import com.chielokacodes.userorgapp.model.Organisation;
import com.chielokacodes.userorgapp.model.User;
import com.chielokacodes.userorgapp.model.UserValidator;
import com.chielokacodes.userorgapp.repository.OrganisationRepository;
import com.chielokacodes.userorgapp.repository.UserRepository;
import com.chielokacodes.userorgapp.services.UserService;
import com.chielokacodes.userorgapp.utils.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final OrganisationRepository organisationRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, UserValidator userValidator, OrganisationRepository organisationRepository){
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
        this.organisationRepository = organisationRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not Found"));
    }

    @Transactional
    public Object saveUser(SignupRequest signupRequest) {

        Errors validationResponse = userValidator.validateUser(signupRequest);
        if (validationResponse != null) {
            return validationResponse; // Return validation errors directly
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPhone(signupRequest.getPhone());
        user.setEmail(signupRequest.getEmail());
        user.setUserRole(Role.USER);

        User createdUser = userRepository.save(user);

        Optional<User> createdUserCheck = userRepository.findByEmail(user.getEmail());
        if (createdUserCheck.isEmpty()) {
            throw new UserNotVerifiedException("Registration unsuccessful");
        }

        Organisation organisation = new Organisation();
        String orgName = createdUser.getFirstName() + "'s Organisation";
        organisation.setName(orgName);

        Set<User> users = new HashSet<>();
        users.add(createdUser);
        organisation.setUsers(users);

        Organisation createdOrg = organisationRepository.save(organisation);

        // Associate the user with the organisation (assuming a bidirectional relationship)
        createdUser.getOrganisationList().add(createdOrg);
        userRepository.save(createdUser); // Update the user to include the organisation


        String accessToken = jwtUtils.createJwt.apply(createdUser);

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setStatus("success");
        successResponse.setMessage("Registration Successful");

        SignupResponse signupResponse = new SignupResponse();
        signupResponse.setUserId(createdUser.getUserId());
        signupResponse.setFirstName(createdUser.getFirstName());
        signupResponse.setLastName(createdUser.getLastName());
        signupResponse.setEmail(createdUser.getEmail());
        signupResponse.setPassword(createdUser.getPassword());
        signupResponse.setPhone(createdUser.getPhone());

        DataResponse data = new DataResponse();
        data.setAccessToken(accessToken);
        data.setUser(signupResponse);

        successResponse.setData(data);

        return successResponse;

    }


    public SuccessResponse logInUser(LoginRequest loginRequest) throws ErrorResponse {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = loadUserByUsername(loginRequest.getEmail());
        User user = userRepository.findUserByEmail(loginRequest.getEmail());


        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            throw new UserNotVerifiedException("Authentication unsuccessful");
        }


        String accessToken = jwtUtils.createJwt.apply((User) authentication.getPrincipal());

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setStatus("success");
        successResponse.setMessage("Login Successful");

        SignupResponse signupResponse = new SignupResponse();
        signupResponse.setUserId(user.getUserId());
        signupResponse.setFirstName(user.getFirstName());
        signupResponse.setLastName(user.getLastName());
        signupResponse.setEmail(user.getEmail());
        signupResponse.setPassword(user.getPassword());
        signupResponse.setPhone(user.getPhone());

        DataResponse data = new DataResponse();
        data.setAccessToken(accessToken);
        data.setUser(signupResponse);

        successResponse.setData(data);

        return successResponse;
    }



    @Transactional()
    public SuccessResponse getUser(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        String username = authentication.getName();
        User loggedInUser = userRepository.findUserByEmail(username);
        User user = userRepository.findUserByUserId(id);

        if(!loggedInUser.equals(user)) {
            throw new UserNotVerifiedException("User is not authenticated");
        }

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setStatus("success");
        successResponse.setMessage("User details gotten successfully");

        SignupResponse signupResponse = new SignupResponse();
        signupResponse.setUserId(user.getUserId());
        signupResponse.setFirstName(user.getFirstName());
        signupResponse.setLastName(user.getLastName());
        signupResponse.setEmail(user.getEmail());
        signupResponse.setPassword(user.getPassword());
        signupResponse.setPhone(user.getPhone());

        DataResponse data = new DataResponse();
        data.setUser(signupResponse);

        successResponse.setData(data);

        return successResponse;
    }


}
