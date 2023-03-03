package com.spring.keycloak.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyCloakUser {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String region;
    private String role;
    private Boolean isAdmin;
    private String userId;


}
