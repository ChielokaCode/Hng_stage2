package com.chielokacodes.userorgapp.controller;

import com.chielokacodes.userorgapp.dto.OrgRequest;
import com.chielokacodes.userorgapp.dto.userToOrg.UserReq;
import com.chielokacodes.userorgapp.exeption.ErrorResponse;
import com.chielokacodes.userorgapp.exeption.SuccessResponse;
import com.chielokacodes.userorgapp.serviceImpl.OrganisationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrganisationController {

    private final OrganisationServiceImpl OrgService;

    @Autowired
    public OrganisationController(OrganisationServiceImpl orgService) {
        OrgService = orgService;
    }

    @GetMapping("/organisations")
    public ResponseEntity<SuccessResponse> getUsers(@AuthenticationPrincipal UserDetails userDetails) {
        SuccessResponse successResponse = OrgService.getOrganisations(userDetails);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @GetMapping("/organisations/{orgId}")
    public ResponseEntity<SuccessResponse> getUserOrganisation(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long orgId) {
        SuccessResponse successResponse = OrgService.getUserOrganisation(userDetails, orgId);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @PostMapping("/organisations")
    public ResponseEntity<SuccessResponse> createOrganisationByUser(@RequestBody OrgRequest orgRequest) throws ErrorResponse {
        SuccessResponse successResponse = OrgService.createOrganisationByUser(orgRequest);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @PostMapping("/organisations/{orgId}/users")
    public ResponseEntity<SuccessResponse> putUserInOrg(@RequestBody UserReq userReq, @PathVariable Long orgId) throws ErrorResponse {
        SuccessResponse successResponse = OrgService.putUserInOrg(userReq, orgId);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }
}
