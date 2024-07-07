package com.chielokacodes.userorgapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Include only non-null fields
public class DataResponse {

   private String accessToken;

   private SignupResponse user;

   private List<OrgResponse> organisations;

   private OrgResponse organisation;
}
