package com.chielokacodes.userorgapp.exeption;

import com.chielokacodes.userorgapp.dto.DataResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Include only non-null fields
public class SuccessResponse {
    private String status;
    private String message;
    private DataResponse data;
}
