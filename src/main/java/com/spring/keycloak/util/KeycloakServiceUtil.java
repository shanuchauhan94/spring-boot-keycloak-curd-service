package com.spring.keycloak.util;

import com.spring.keycloak.model.KeyCloakUser;
import com.spring.keycloak.model.ResponseContext;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component
public class KeycloakServiceUtil {

    /**
     * get our new role as a Role Representation and make it a composite role by adding “offline_access” role to it
     */
    public static void makeComposite(String role_name, RealmResource realmResource) {

        RoleRepresentation role = realmResource.roles().get(role_name).toRepresentation();

        List<RoleRepresentation> composites = new LinkedList<>();
        RoleRepresentation role1 = realmResource.roles().get("offline_access").toRepresentation();
        RoleRepresentation role2 = realmResource.roles().get("uma_authorization").toRepresentation();
        composites.add(role1);
        composites.add(role2);
        realmResource.rolesById().addComposites(role.getId(), composites);
    }

    public static ResponseContext buildExceptionResponseContext(KeyCloakUser cloakUser, Exception e) {

        int status = 400;
        String message = e.getMessage();
        String code = StringUtils.substringBefore(StringUtils.substringAfter(e.getMessage(), "HTTP "), " ");
        if (Objects.equals(code, "409")) {
            message = e.getMessage() + " ROLE -> " + cloakUser.getRole() + " <- already exits!. create new ROLE.";
        }
        if (StringUtils.isNoneBlank(code))
            status = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfter(e.getMessage(), "HTTP "), " "));
        return new ResponseContext(status, message);
    }

    public static void buildUserRepresentation(KeyCloakUser cloakUser, CredentialRepresentation credentialRepresentation, UserRepresentation userRepresentation) {

        userRepresentation.setUsername(cloakUser.getUsername());
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        userRepresentation.setFirstName(cloakUser.getFirstName());
        userRepresentation.setLastName(cloakUser.getLastName());
        userRepresentation.setEmail(cloakUser.getEmail());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(false);
    }

    public static void clientRoleAssignService(RealmResource realmResource, RoleMappingResource roleMappingResource, String clientId) {

        realmResource.clients().findByClientId(clientId).stream().map(ClientRepresentation::getId).findAny().ifPresent(role -> {
            RoleRepresentation clientRole = realmResource.clients().get(role).roles().get("USER").toRepresentation();
            roleMappingResource.clientLevel(role).add(Collections.singletonList(clientRole));
        });
    }

    public static void realmRoleAssignService(RealmResource realmResource, KeyCloakUser cloakUser, RoleMappingResource roleMappingResource) {

        if (cloakUser.getIsAdmin()) {
            // realm role
            realmResource.roles().list()
                    .stream()
                    .map(RoleRepresentation::getName)
                    .filter(name -> name.startsWith(cloakUser.getRegion()))
                    .map(m -> realmResource.roles().get(m).toRepresentation())
                    .findAny()
                    .ifPresent(realmRole -> roleMappingResource.realmLevel().add(Collections.singletonList(realmRole)));
        } else {
            // custom role assigned
            RoleRepresentation roleRep = new RoleRepresentation();
            roleRep.setName(cloakUser.getRole());
            roleRep.setDescription("services allowed of role " + cloakUser.getRole() + " for " + cloakUser.getRegion() + " region");
            realmResource.roles().create(roleRep);

            makeComposite(cloakUser.getRole(), realmResource);

            RoleRepresentation newRole = realmResource.roles().get(cloakUser.getRole()).toRepresentation();
            roleMappingResource.realmLevel().add(Collections.singletonList(newRole));
        }
    }

    public static void groupAssignService(RealmResource realmResource, String userId, String region) {

        realmResource.groups().groups()
                .stream()
                .filter(f -> f.getName().startsWith(region))
                .peek(groupRepresentation -> realmResource.users().get(userId).joinGroup(groupRepresentation.getId()))
                .findAny()
                .ifPresent(group -> System.err.println("Group " + group.getName() + " assigned."));

    }
}
