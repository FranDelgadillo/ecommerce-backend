package com.shirtcompany.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("orders")
public class Order {
    @Id
    private Long id;
    private Long userId;
    private String status;

    private transient List<OrderItem> items;
}
