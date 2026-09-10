package com.enviro.assessment.junior.nonhle.repository;

import com.enviro.assessment.junior.nonhle.entity.Investor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Data access for Investor. Extending JpaRepository gives us save(),
 * findById(), findAll(), delete(), etc. for free - no implementation needed,
 * Spring Data generates it at runtime.
 */
public interface InvestorRepository extends JpaRepository<Investor, Long> {

    Optional<Investor> findByEmail(String email);
}
