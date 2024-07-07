package com.chielokacodes.userorgapp.repository;

import com.chielokacodes.userorgapp.model.Organisation;
import com.chielokacodes.userorgapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    List<Organisation> findByUsers(User user);
    Organisation findByUsersAndOrgId(User user, Long orgId);

    Object findAllByUsersContains(User testUser);
}
