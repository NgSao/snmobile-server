package com.snd.server.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "config_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String storePhone;
    String storeAddress;

    String districtName;
    String wardName;
    String city;
    Integer districtId;
    String wardCode;
    Double latitude;
    Double longitude;

    @ManyToOne
    @JoinColumn(name = "config_id")
    Config config;
}
