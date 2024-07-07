package com.chielokacodes.userorgapp.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrgResponse {
    private Long orgId;
    private String name;
    private String description;
}
