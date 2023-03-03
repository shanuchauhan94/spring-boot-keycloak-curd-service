package com.spring.keycloak.service.impl;

import com.spring.keycloak.config.KeycloakProvider;
import com.spring.keycloak.model.KeyCloakUser;
import com.spring.keycloak.model.ResponseContext;
import com.spring.keycloak.service.KeycloakUserService;
import com.spring.keycloak.util.KeycloakServiceUtil;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Service
public class KeycloakServiceImpl implements KeycloakUserService {

    private final KeycloakProvider provider;
    private final RealmResource realmResource;
    private final UsersResource usersResource;


    @Autowired
    public KeycloakServiceImpl(KeycloakProvider provider) {
        this.provider = provider;
        Keycloak keycloak = provider.getKeycloakInstance();
        this.realmResource = keycloak.realm(provider.REALM);
        this.usersResource = realmResource.users();
    }

    @Override
    public ResponseContext createKeycloakUser(KeyCloakUser cloakUser) {

        ResponseContext responseContext;
        // create password credential
        CredentialRepresentation credentialRepresentation = provider.createPasswordCredentials(cloakUser.getPassword());

        UserRepresentation userRepresentation = new UserRepresentation();
        // userRepresentation creation
        KeycloakServiceUtil.buildUserRepresentation(cloakUser, credentialRepresentation, userRepresentation);
        try {
            Response response = usersResource.create(userRepresentation);
            responseContext = new ResponseContext(response.getStatus(), response.getStatusInfo().getReasonPhrase());
            if (response.getStatus() == 201) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                responseContext.setUserId(userId);
                System.err.println("Created userId " + userId);
                // Assigned ROLE(CLIENT AND REALM)
                assignClientAndRealmRole(realmResource, usersResource, userId, cloakUser);
                // Assigned Group
                KeycloakServiceUtil.groupAssignService(realmResource, userId, cloakUser.getRegion());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return KeycloakServiceUtil.buildExceptionResponseContext(cloakUser, e);
        }
        return responseContext;
    }

    private void assignClientAndRealmRole(RealmResource realmResource, UsersResource usersResource, String userId, KeyCloakUser cloakUser) {

        RoleMappingResource roleMappingResource = usersResource.get(userId).roles();
        // client role
        KeycloakServiceUtil.clientRoleAssignService(realmResource, roleMappingResource, provider.CLIENT_ID);
        // realm role
        KeycloakServiceUtil.realmRoleAssignService(realmResource, cloakUser, roleMappingResource);
    }

    @Override
    public ResponseContext deleteKeycloakUser(String userId) {

        Keycloak keycloak = provider.getKeycloakInstance();
        RealmResource realmResource = keycloak.realm(provider.REALM);
        UsersResource usersResource = realmResource.users();
        usersResource.get(userId).remove();
        return new ResponseContext(HttpStatus.ACCEPTED.value(), "User Deleted Successfully.");
    }

    @Override
    public List<UserRepresentation> searchKeycloakUserByName(String userName) {
        return usersResource.search(userName, true);
    }

    @Override
    public String updateKeycloakUser(KeyCloakUser cloakUser) {

        CredentialRepresentation credential = provider.createPasswordCredentials(cloakUser.getPassword());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(cloakUser.getUsername());
        user.setFirstName(cloakUser.getFirstName());
        user.setLastName(cloakUser.getLastName());
        user.setEmail(cloakUser.getEmail());
        user.setCredentials(Collections.singletonList(credential));

        usersResource.get(cloakUser.getUserId()).update(user);
        return cloakUser.getUserId() + " updated successfully.";
    }

}
