package com.chielokacodes.userorgapp.exeption;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse extends RuntimeException {
        private String status;
        private String message;
        private String statusCode;

        public ErrorResponse(String status, String message, String statusCode) {
                super(message);
                this.status = status;
                this.message = message;
                this.statusCode = statusCode;
        }
}
