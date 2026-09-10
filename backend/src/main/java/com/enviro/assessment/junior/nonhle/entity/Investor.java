package com.enviro.assessment.junior.nonhle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * A person who holds one or more investment portfolios with Enviro365.
 *
 * dateOfBirth is stored (not just an "age" field) because age changes over
 * time - storing the raw date of birth and calculating age on demand keeps
 * the data correct indefinitely, which matters for the "age > 65" rule.
 */
@Entity
@Table(name = "investors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Investor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "investor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Portfolio> portfolios = new ArrayList<>();

    /**
     * Calculates current age from dateOfBirth. Used directly by the
     * "retirement withdrawals only allowed if age > 65" business rule.
     */
    @Transient
    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
