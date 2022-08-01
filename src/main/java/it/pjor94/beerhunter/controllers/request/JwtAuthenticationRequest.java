package it.pjor94.beerhunter.controllers.request;

import lombok.Data;

@Data
public class JwtAuthenticationRequest {
    String username;
    String password;
}
