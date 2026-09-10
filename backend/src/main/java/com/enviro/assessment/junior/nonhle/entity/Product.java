package com.enviro.assessment.junior.nonhle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An investment product Enviro365 offers (e.g. a Retirement Annuity or a
 * Unit Trust). A Portfolio links an Investor to one of these.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    /**
     * Convenience check used by the withdrawal business rules:
     * retirement withdrawals need the investor to be older than 65.
     */
    public boolean isRetirementProduct() {
        return type == ProductType.RETIREMENT_ANNUITY;
    }
}
