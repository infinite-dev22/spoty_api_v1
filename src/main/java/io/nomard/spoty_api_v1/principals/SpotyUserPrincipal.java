package io.nomard.spoty_api_v1.principals;

import io.nomard.spoty_api_v1.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SpotyUserPrincipal implements UserDetails {
    private final User user;

    private List<GrantedAuthority> permissions;
    private ArrayList<String> permissionNames;

    public SpotyUserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.permissions;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    public List<GrantedAuthority> getPermissions() {
        user.getRole().getPermissions().forEach(permission -> permissionNames.add(permission.getName()));
        this.permissions = permissionNames.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return this.permissions;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}