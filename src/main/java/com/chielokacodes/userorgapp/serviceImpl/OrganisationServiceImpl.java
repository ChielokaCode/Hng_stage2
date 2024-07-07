package com.chielokacodes.userorgapp.serviceImpl;

import com.chielokacodes.userorgapp.dto.DataResponse;
import com.chielokacodes.userorgapp.dto.OrgRequest;
import com.chielokacodes.userorgapp.dto.OrgResponse;
import com.chielokacodes.userorgapp.dto.userToOrg.UserReq;
import com.chielokacodes.userorgapp.exeption.ErrorResponse;
import com.chielokacodes.userorgapp.exeption.SuccessResponse;
import com.chielokacodes.userorgapp.model.Organisation;
import com.chielokacodes.userorgapp.model.User;
import com.chielokacodes.userorgapp.repository.OrganisationRepository;
import com.chielokacodes.userorgapp.repository.UserRepository;
import com.chielokacodes.userorgapp.services.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrganisationServiceImpl implements OrgService {

    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;


    @Autowired
    public OrganisationServiceImpl(OrganisationRepository organisationRepository, OrganisationRepository organisationRepository1, UserRepository userRepository) {
        this.organisationRepository = organisationRepository1;
        this.userRepository = userRepository;
    }


    public SuccessResponse getOrganisations(UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findUserByEmail(username);
        List<Organisation> organizations = organisationRepository.findByUsers(user);

        OrgResponse orgResponse = new OrgResponse();
        for(Organisation organisation : organizations) {
            orgResponse.setOrgId(organisation.getOrgId());
            orgResponse.setName(organisation.getName());
            orgResponse.setDescription(organisation.getDescription());
        }
        List<OrgResponse> organizationList = new ArrayList<>();
        organizationList.add(orgResponse);

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setStatus("success");
        successResponse.setMessage("User organisations gotten successfully");

        DataResponse data = new DataResponse();
        data.setOrganisations(organizationList);

        successResponse.setData(data);

        return successResponse;
    }



    public SuccessResponse getUserOrganisation(UserDetails userDetails, Long orgId) {
        String username = userDetails.getUsername();
        User user = userRepository.findUserByEmail(username);

        Organisation organisation = organisationRepository.findByUsersAndOrgId(user, orgId);

        OrgResponse orgResponse = new OrgResponse();
        orgResponse.setOrgId(organisation.getOrgId());
        orgResponse.setName(organisation.getName());
        orgResponse.setDescription(organisation.getDescription());

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setStatus("success");
        successResponse.setMessage("User organisation gotten successfully");

        DataResponse data = new DataResponse();

        data.setOrganisation(orgResponse);

        successResponse.setData(data);

        return successResponse;
    }

    public SuccessResponse createOrganisationByUser(OrgRequest orgRequest) throws ErrorResponse {

        if (orgRequest.getName() == null || orgRequest.getName().isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setStatus("Bad request");
            errorResponse.setMessage("Client error");
            errorResponse.setStatusCode("400");
            throw errorResponse;
        } else {
            Organisation organisation = new Organisation();
            organisation.setName(orgRequest.getName());
            organisation.setDescription(orgRequest.getDescription());
            Organisation createdOrg = organisationRepository.save(organisation);

            OrgResponse orgResponse = new OrgResponse();
            orgResponse.setOrgId(createdOrg.getOrgId());
            orgResponse.setName(createdOrg.getName());
            orgResponse.setDescription(createdOrg.getDescription());

            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setStatus("success");
            successResponse.setMessage("Organisation created successfully");

            DataResponse data = new DataResponse();

            data.setOrganisation(orgResponse);

            successResponse.setData(data);

            return successResponse;
        }
    }

    public SuccessResponse putUserInOrg(UserReq userReq, Long orgId) throws ErrorResponse {
        Optional<Organisation> organisation = organisationRepository.findById(orgId);
        User user = userRepository.findUserByUserId(Long.parseLong(userReq.getUserId()));

        if (user == null || organisation.get().getUsers().isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setStatus("Bad request");
            errorResponse.setMessage("user or organisation is incorrect");
            errorResponse.setStatusCode("400");
            throw errorResponse;
        } else {

            organisation.get().addUser(user);
            organisationRepository.save(organisation.get());

            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setStatus("success");
            successResponse.setMessage("User added to organisation successfully");

            return successResponse;
        }
    }


}
