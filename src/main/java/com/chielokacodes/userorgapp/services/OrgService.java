package com.chielokacodes.userorgapp.services;

import com.chielokacodes.userorgapp.dto.OrgRequest;
import com.chielokacodes.userorgapp.dto.userToOrg.UserReq;
import com.chielokacodes.userorgapp.exeption.ErrorResponse;
import com.chielokacodes.userorgapp.exeption.SuccessResponse;

public interface OrgService {
    SuccessResponse getOrganisations();

    SuccessResponse getUserOrganisation(Long orgId);

    Object createOrganisationByUser(OrgRequest orgRequest) throws ErrorResponse;

    Object putUserInOrg(UserReq userReq, Long orgId) throws ErrorResponse;
}
