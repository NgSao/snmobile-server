package com.snd.server.model;

import java.util.Set;

import com.snd.server.enums.GuardNameEnum;
import com.snd.server.enums.RoleEnum;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    RoleEnum name;

    @Enumerated(EnumType.STRING)
    GuardNameEnum guardName;

    @OneToMany(mappedBy = "role")
    Set<User> users;

}