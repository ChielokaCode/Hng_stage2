package com.chielokacodes.userorgapp.model;


import com.chielokacodes.userorgapp.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @JsonIgnore
    private Long userId;

    @NonNull
    private String firstName;
    @NonNull
    private String lastName;


    @Column(unique = true)
    @NonNull
    private String email;



    private String password;
    private String phone;

    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private Role userRole;

    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "user_organisation",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "organisation_id")
    )
    private Set<Organisation> organisationList = new HashSet<>();


    @JsonIgnore
    private Boolean isAcctEnabled = true;

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(Collections.singleton(new SimpleGrantedAuthority(this.userRole.name())));
    }

    @JsonIgnore
    public String getUsername() {
        return this.email;
    }

    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isEnabled() {
        return this.isAcctEnabled;
    }

    public void addOrganization(Organisation organization) {
        this.organisationList.add(organization);
        organization.getUsers().add(this);
    }

    public void removeOrganization(Organisation organization) {
        this.organisationList.remove(organization);
        organization.getUsers().remove(this);
    }
}
