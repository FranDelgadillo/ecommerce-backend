package com.shirtcompany.payment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = PaymentServiceApplication.class,
        properties = {"spring.r2dbc.url=r2dbc:h2:mem:///testdb", "spring.r2dbc.username=sa", "spring.r2dbc.password="}
)
@ActiveProfiles("test")
class PaymentServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}