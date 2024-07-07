package com.chielokacodes.userorgapp.serviceImpl;

import com.chielokacodes.userorgapp.dto.DataResponse;
import com.chielokacodes.userorgapp.dto.OrgRequest;
import com.chielokacodes.userorgapp.dto.OrgResponse;
import com.chielokacodes.userorgapp.dto.userToOrg.UserReq;
import com.chielokacodes.userorgapp.exeption.ErrorResponse;
import com.chielokacodes.userorgapp.exeption.SuccessResponse;
import com.chielokacodes.userorgapp.exeption.UserNotVerifiedException;
import com.chielokacodes.userorgapp.model.Organisation;
import com.chielokacodes.userorgapp.model.User;
import com.chielokacodes.userorgapp.repository.OrganisationRepository;
import com.chielokacodes.userorgapp.repository.UserRepository;
import com.chielokacodes.userorgapp.services.OrgService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrganisationServiceImpl implements OrgService {

    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;


    @Autowired
    public OrganisationServiceImpl(OrganisationRepository organisationRepository, OrganisationRepository organisationRepository1, UserRepository userRepository) {
        this.organisationRepository = organisationRepository1;
        this.userRepository = userRepository;
    }


    public SuccessResponse getOrganisations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        String username = authentication.getName();

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



    public SuccessResponse getUserOrganisation(Long orgId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

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

    public Object createOrganisationByUser(OrgRequest orgRequest) throws ErrorResponse {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findUserByEmail(username);

        if (orgRequest.getName() == null || orgRequest.getName().isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setStatus("Bad request");
            errorResponse.setMessage("Client error");
            errorResponse.setStatusCode("400");
            throw errorResponse;
        } else {
//            List<Organisation> userOrgList = organisationRepository.findByUsers(user);

            Organisation organisation = new Organisation();
            organisation.setName(orgRequest.getName());
            organisation.setDescription(orgRequest.getDescription());
            Organisation createdOrg = organisationRepository.save(organisation);

//            userOrgList.add(createdOrg);
            user.getOrganisationList().add(createdOrg);
            userRepository.save(user); // Update the user to include the organisation

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

    @Transactional
    public Object putUserInOrg(UserReq userReq, Long orgId) throws ErrorResponse {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotVerifiedException("User not Authenticated");
        }

        String username = authentication.getName();

        User loggedInUser = userRepository.findUserByEmail(username);


        Organisation organisation = organisationRepository.findById(orgId).get();

        User user = userRepository.findUserByUserId(Long.parseLong(userReq.getUserId()));


            organisation.addUser(user);
            organisationRepository.save(organisation);

            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setStatus("success");
            successResponse.setMessage("User added to organisation successfully");

            return successResponse;
    }


}
