package com.shirtcompany.product.repository;

import com.shirtcompany.product.model.Size;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SizeRepository extends ReactiveCrudRepository<Size, Long> {
}