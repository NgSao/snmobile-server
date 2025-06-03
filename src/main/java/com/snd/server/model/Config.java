package com.snd.server.model;

import java.util.Set;

import com.snd.server.enums.EntityStatusEnum;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "config")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Config extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String storeName;
    String storeEmail;
    String storePhone;
    String storeAddress;
    String storeLogo;
    String storeDescription;
    Integer freeShippingThreshold;

    @OneToMany(mappedBy = "config", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ConfigDetail> branches;

    public void markAsConfigDetail(EntityStatusEnum statusEnum) {
        this.setStatus(statusEnum);
        if (this.branches != null) {
            this.branches.forEach(b -> b.setStatus(statusEnum));
        }
    }
}
