package com.hpms.userservice.model;

import com.hpms.userservice.constants.AuthProviders;
import com.hpms.commonlib.constants.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(indexes = {@Index(name = "user_username_idx", columnList = "username")})
@Inheritance(strategy = InheritanceType.JOINED)
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class User implements OAuth2User, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String username;

    private String password;

    private Boolean locked = true;

    private Boolean enabled = false;

    @Enumerated(EnumType.STRING)
    private AuthProviders provider;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean deleted = false;

    private boolean receiveNotifications = true;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private DeletionRequest deletionRequest;
//
//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "creator")
//    private List<OfferComment> offerComment;

    @Transient
    private Map<String, Object> attributes;

    public User() {
    }

    public User(String username, String password, Boolean locked, Boolean enabled,
                AuthProviders provider, String providerId, RoleEnum role,
                Map<String, Object> attributes) {
        this.username = username;
        this.password = password;
        this.locked = locked;
        this.enabled = enabled;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return this.id.toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
