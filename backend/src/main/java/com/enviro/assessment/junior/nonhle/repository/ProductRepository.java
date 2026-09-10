package com.enviro.assessment.junior.nonhle.repository;

import com.enviro.assessment.junior.nonhle.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
