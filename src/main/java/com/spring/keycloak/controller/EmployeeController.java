package com.spring.keycloak.controller;

import com.spring.keycloak.model.KeyCloakUser;
import com.spring.keycloak.model.ResponseContext;
import com.spring.keycloak.service.KeycloakUserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/app/keycloak")
public class EmployeeController {

    private final KeycloakUserService keycloakUserService;

    @Autowired
    public EmployeeController(KeycloakUserService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }

    @GetMapping("/")
    public ResponseEntity<String> welcomeVisitors() {
        return new ResponseEntity<>("************ Welcome To Keycloak Service *******************", HttpStatus.OK);
    }

    @PostMapping("/user/create")
    @RolesAllowed({"ind-admin"})
    public ResponseEntity<ResponseContext> createKeycloakUsers(@RequestBody KeyCloakUser cloakUser) {

        if (Objects.nonNull(cloakUser)) {
            ResponseContext response = keycloakUserService.createKeycloakUser(cloakUser);
            return new ResponseEntity<>(new ResponseContext(response.getStatus(), response.getReason(), response.getUserId()),
                    HttpStatus.valueOf(response.getStatus()));
        }
        return new ResponseEntity<>(new ResponseContext(400, "Bad Request"), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/user/update")
    @RolesAllowed({"ind-admin"})
    public ResponseEntity<String> updateKeycloakUsers(@RequestBody KeyCloakUser cloakUser) {

        String response = keycloakUserService.updateKeycloakUser(cloakUser);
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @DeleteMapping("/user/delete/{userId}")
    @RolesAllowed({"ind-admin"})
    public ResponseEntity<ResponseContext> deleteKeycloakUsers(@PathVariable("userId") String userId) {

        ResponseContext response = keycloakUserService.deleteKeycloakUser(userId);
        return new ResponseEntity<>(new ResponseContext(response.getStatus(), response.getReason(), response.getUserId()),
                HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("/user/{userName}")
    @RolesAllowed({"ind-admin", "us-admin"})
    public ResponseEntity<List<UserRepresentation>> readKeycloakUsers(@PathVariable("userName") String userName) {

        List<UserRepresentation> response = keycloakUserService.searchKeycloakUserByName(userName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
