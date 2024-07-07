package com.chielokacodes.userorgapp.services;

import com.chielokacodes.userorgapp.dto.OrgRequest;
import com.chielokacodes.userorgapp.dto.userToOrg.UserReq;
import com.chielokacodes.userorgapp.exeption.ErrorResponse;
import com.chielokacodes.userorgapp.exeption.SuccessResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface OrgService {
    SuccessResponse getOrganisations(UserDetails userDetails);

    SuccessResponse getUserOrganisation(UserDetails userDetails, Long orgId);

    SuccessResponse createOrganisationByUser(OrgRequest orgRequest) throws ErrorResponse;

    SuccessResponse putUserInOrg(UserReq userReq, Long orgId) throws ErrorResponse;
}
