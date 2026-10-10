package com.worksphere.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.worksphere.entity.User;

public class CustomUserDetails implements UserDetails {

    public final User user;

    public CustomUserDetails(User u) {
        user = u;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.role.name()));
    }

    public String getPassword() {
        return user.password;
    }

    public String getUsername() {
        return user.email;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return user.active;
    }
}
