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

    String fullName;

    String email;

    String password;

    String phone;

    Instant birthday;

    String profileImageUrl;

    Instant lastLoginDate;

    @Enumerated(EnumType.STRING)
    GenderEnum gender;

    @Column(columnDefinition = "TEXT")
    String refreshToken;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<Address> addresses = new HashSet<>();

}