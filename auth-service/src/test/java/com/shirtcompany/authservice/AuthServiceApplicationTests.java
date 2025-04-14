package com.shirtcompany.authservice;

import com.shirtcompany.auth.AuthServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = AuthServiceApplication.class,
        properties = {"spring.r2dbc.url=r2dbc:h2:mem:///testdb", "spring.r2dbc.username=sa", "spring.r2dbc.password="}
)
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}