package com.shirtcompany.product;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = ProductServiceApplication.class,
        properties = {"spring.r2dbc.url=r2dbc:h2:mem:///testdb", "spring.r2dbc.username=sa", "spring.r2dbc.password="}
)
@ActiveProfiles("test")
class ProductServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}