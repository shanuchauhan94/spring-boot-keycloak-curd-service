package com.spring.keycloak.service;

import com.spring.keycloak.model.KeyCloakUser;
import com.spring.keycloak.model.ResponseContext;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface KeycloakUserService {
    ResponseContext createKeycloakUser(KeyCloakUser cloakUser);

    ResponseContext deleteKeycloakUser(String userId);

    List<UserRepresentation> searchKeycloakUserByName(String userName);

    String updateKeycloakUser(KeyCloakUser cloakUser);
}
