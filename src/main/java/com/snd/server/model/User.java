package com.snd.server.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.snd.server.enums.GenderEnum;
import com.snd.server.enums.ProviderEnum;
import com.snd.server.enums.UserStatusEnum;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "full_name", nullable = false, length = 255)
    String fullName;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    String email;

    @Column(name = "phone", length = 20, unique = true)
    String phone;

    @Column(name = "avatar", length = 255)
    String avatar;

    @Column(name = "password", nullable = false, length = 255)
    String password;

    @Enumerated(EnumType.STRING)
    GenderEnum gender;

    @Column(name = "date_of_birth")
    Instant dateOfBirth;

    @Column(name = "address", length = 355)
    String address;

    @Column(name = "agency_limit")
    Integer agencyLimit;

    @Column(columnDefinition = "TEXT")
    String rememberToken;

    @Enumerated(EnumType.STRING)
    UserStatusEnum userStatus;

    @Enumerated(EnumType.STRING)
    ProviderEnum provider;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<Session> sessions = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<Provider> providers = new HashSet<>();

}