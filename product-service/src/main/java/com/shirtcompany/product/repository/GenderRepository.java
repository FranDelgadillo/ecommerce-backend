package com.shirtcompany.product.repository;

import com.shirtcompany.product.model.Gender;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenderRepository extends ReactiveCrudRepository<Gender, Long> {
}
