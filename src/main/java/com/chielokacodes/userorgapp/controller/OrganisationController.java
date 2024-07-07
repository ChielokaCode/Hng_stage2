package com.chielokacodes.userorgapp.controller;

import com.chielokacodes.userorgapp.dto.OrgRequest;
import com.chielokacodes.userorgapp.dto.userToOrg.UserReq;
import com.chielokacodes.userorgapp.exeption.ErrorResponse;
import com.chielokacodes.userorgapp.exeption.SuccessResponse;
import com.chielokacodes.userorgapp.serviceImpl.OrganisationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SuccessResponse> getUsers() {
        SuccessResponse successResponse = OrgService.getOrganisations();
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @GetMapping("/organisations/{orgId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SuccessResponse> getUserOrganisation(@PathVariable Long orgId) {
        SuccessResponse successResponse = OrgService.getUserOrganisation(orgId);
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @PostMapping("/organisations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrganisationByUser(@RequestBody OrgRequest orgRequest) throws ErrorResponse {
        Object response = OrgService.createOrganisationByUser(orgRequest);

        if (response instanceof ErrorResponse) {
            // Handle validation errors
            return ResponseEntity.badRequest().body(((ErrorResponse) response));
        } else if (response instanceof SuccessResponse) {
            // Return success response
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            // Handle unexpected case, though ideally shouldn't occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected response type");
        }
    }


    @PostMapping("/organisations/{orgId}/users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> putUserInOrg(@RequestBody UserReq userReq, @PathVariable Long orgId) throws ErrorResponse {
        Object response = OrgService.putUserInOrg(userReq, orgId);

        if (response instanceof ErrorResponse) {
            // Handle validation errors
            return ResponseEntity.badRequest().body(((ErrorResponse) response));
        } else if (response instanceof SuccessResponse) {
            // Return success response
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            // Handle unexpected case, though ideally shouldn't occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected response type");
        }
    }
}
