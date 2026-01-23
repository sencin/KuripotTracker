package com.mintblueberry.KuripotTracker.config;
import com.mintblueberry.KuripotTracker.entity.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    @NonNull
    private final User user; // Lombok constructor ensures non-null

    @NonNull
    public User getUser() {
        return Objects.requireNonNull(user, "User cannot be null");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getUser().getRoles() // now IDE knows user is non-null
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getErole().name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return getUser().getPassword();
    }

    @Override
    public String getUsername() {
        return getUser().getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}