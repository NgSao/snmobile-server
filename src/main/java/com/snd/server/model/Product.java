package com.snd.server.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.snd.server.enums.EntityStatusEnum;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String slug;

    String sku;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(columnDefinition = "TEXT")
    String specification;

    @Column(columnDefinition = "TEXT")
    String promotions;

    int stock;

    int sold;

    float rating;
    int ratingCount;

    BigDecimal originalPrice;

    BigDecimal salePrice;
    BigDecimal importPrice;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    Brand brand;

    @ManyToMany
    @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    Set<Media> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    Set<Variant> variants = new HashSet<>();

    public void markAsStatus(EntityStatusEnum statusEnum) {
        this.setStatus(statusEnum);
        if (this.variants != null) {
            this.variants.forEach(b -> b.setStatus(statusEnum));
        }
    }

}
