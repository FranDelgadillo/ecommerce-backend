package com.shirtcompany.authservice;

import com.shirtcompany.auth.AuthServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = AuthServiceApplication.class,
        properties = {"spring.r2dbc.url=r2dbc:h2:mem:///testdb", "spring.r2dbc.username=sa", "spring.r2dbc.password="}
)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}