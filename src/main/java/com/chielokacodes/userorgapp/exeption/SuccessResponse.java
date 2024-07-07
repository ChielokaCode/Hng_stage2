package com.chielokacodes.userorgapp.exeption;

import com.chielokacodes.userorgapp.dto.DataResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponse {
    private String status;
    private String message;
    private DataResponse data;
}
