package com.chielokacodes.userorgapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataResponse {

   private String accessToken;

   private SignupResponse user;

   private List<OrgResponse> organisations;

   private OrgResponse organisation;
}
