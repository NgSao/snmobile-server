package com.snd.server.model;

import com.snd.server.enums.AddressType;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Enumerated(EnumType.STRING)
    AddressType addressType;

    String fullName;
    String phone;
    String city;
    String district;
    String street;
    String addressDetail;
    Boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

}