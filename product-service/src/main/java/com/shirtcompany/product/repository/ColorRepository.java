package com.shirtcompany.product.repository;

import com.shirtcompany.product.model.Color;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ColorRepository extends ReactiveCrudRepository<Color, Integer> {
}