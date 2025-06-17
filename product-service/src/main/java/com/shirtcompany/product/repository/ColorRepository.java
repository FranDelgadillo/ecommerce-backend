package com.shirtcompany.product.repository;

import com.shirtcompany.product.model.Color;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends ReactiveCrudRepository<Color, Long> {
}