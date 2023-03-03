package com.spring.keycloak.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseContext {

    private int status;
    private String reason;
    private String timestamp;
    private String userId;

    public ResponseContext(int status, String reason) {
        this.status = status;
        this.reason = reason;
        this.timestamp = getCurrentTimestamp();
    }

    public ResponseContext(int status, String reason, String userId) {
        this.status = status;
        this.reason = reason;
        this.userId = userId;
        this.timestamp = getCurrentTimestamp();

    }

    private String getCurrentTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }


}
