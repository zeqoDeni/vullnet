package org.vullnet.vullnet00.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.vullnet.vullnet00.model.Role;
import org.vullnet.vullnet00.model.User;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final boolean active;

    public UserPrincipal(Long id, String email, String passwordHash, Role role, boolean active) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

    public static UserPrincipal fromUser(User user) {
        return new UserPrincipal(user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole(), user.isActive());
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = role != null ? role.name() : "USER";
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
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
        return active;
    }
}
