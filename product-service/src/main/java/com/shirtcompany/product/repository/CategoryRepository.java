package com.shirtcompany.product.repository;

import com.shirtcompany.product.model.Category;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CategoryRepository extends ReactiveCrudRepository<Category, Long> {
}