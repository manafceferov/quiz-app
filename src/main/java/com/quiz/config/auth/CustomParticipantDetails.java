package com.quiz.config.auth;

import com.quiz.entity.Participant;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomParticipantDetails implements UserDetails {
    private final Participant participant;
    private final Long id;
    private final String email;
    private final String password;
    private final String role;

    public CustomParticipantDetails(Participant participant) {
        this.participant = participant;
        this.id = participant.getId();
        this.email = participant.getEmail();
        this.password = participant.getPassword();
        this.role = participant.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_PARTICIPANT"));
    }

    @Override
    public String getPassword() {
        return participant.getPassword();
    }

    @Override
    public String getUsername() {
        return participant.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
