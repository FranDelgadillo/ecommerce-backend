package com.shirtcompany.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("payments")
public class Payment {
    @Id
    private Long id;
    private Long orderId;
    private Double amount;
    private String status;
}
