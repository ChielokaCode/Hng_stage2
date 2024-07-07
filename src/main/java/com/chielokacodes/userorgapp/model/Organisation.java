package com.chielokacodes.userorgapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "org")
public class Organisation {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "org_id")
   @JsonIgnore
   private Long orgId;

   @NonNull
   @Column(name = "org_name", nullable = false)
   private String name;
   private String description;

   @JsonIgnore
   @ManyToMany(mappedBy = "organisationList")
   private Set<User> users = new HashSet<>();

   public void addUser(User user) {
      this.users.add(user);
      user.getOrganisationList().add(this);
   }

   public void removeUser(User user) {
      this.users.remove(user);
      user.getOrganisationList().remove(this);
   }
}
