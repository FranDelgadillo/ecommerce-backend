package com.shirtcompany.product.repository;

import com.shirtcompany.product.model.Size;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepository extends ReactiveCrudRepository<Size, Long> {
}