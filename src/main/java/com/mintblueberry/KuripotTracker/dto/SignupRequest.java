package com.mintblueberry.KuripotTracker.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String firstName;
    private String middleName;
    private String lastName;
    private String extensionName;
    private String email;
    private String password;
}