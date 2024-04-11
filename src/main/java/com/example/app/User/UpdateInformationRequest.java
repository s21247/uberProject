package com.example.app.User;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateInformationRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

}
