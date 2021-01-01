package it.pjor94.beerhunter.controllers.response;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class JwtAuthenticationResponse {
    String username;
    Collection<? extends GrantedAuthority> authorities;
    String token;

    public JwtAuthenticationResponse(String username, Collection<? extends GrantedAuthority> authorities, String token) {
        this.username=username;
        this.authorities=authorities;
        this.token=token;
    }
}
