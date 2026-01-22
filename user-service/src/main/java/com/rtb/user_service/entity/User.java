package com.rtb.user_service.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String email;

    private String password;

    private Boolean isActive;

    private String phone;

    private Boolean isEmailVerified;

    private Set<Role> roles = new HashSet<>();

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    private void onCreate(){
        createdAt = Instant.now();
        updatedAt = Instant.now();
        isActive = true;
        isEmailVerified = false;
    }

    @PreUpdate
    private void onUpdate(){
        updatedAt = Instant.now();
    }
}
