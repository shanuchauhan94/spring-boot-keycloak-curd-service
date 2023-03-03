package com.spring.keycloak.config;

import lombok.Getter;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KeycloakProvider {

    @Value("${keycloak.auth-server-url}")
    public String SERVER_URL;
    @Value("${keycloak.realm}")
    public String REALM;
    @Value("${keycloak.resource}")
    public String CLIENT_ID;
    @Value("${keycloak.credentials.secret}")
    public String CLIENT_SECRET;
    @Value("${keycloak.credentials.username}")
    public String USER_NAME;
    @Value("${keycloak.credentials.password}")
    public String USER_PASSWORD;

    private static Keycloak keycloak;

    public KeycloakProvider() {
    }

    public Keycloak getKeycloakInstance() {

        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(SERVER_URL)
                    .grantType(OAuth2Constants.PASSWORD)
                    .realm(REALM)
                    .clientId(CLIENT_ID)
                    .clientSecret(CLIENT_SECRET)
                    .username(USER_NAME)
                    .password(USER_PASSWORD)
                    .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
                    .build();
        }
        return keycloak;
    }

    public CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }


}
