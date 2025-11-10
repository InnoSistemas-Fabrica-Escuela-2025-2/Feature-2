package com.udea.innosistemas.innosistemas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class InnosistemasApplicationTests {

	@Test
	void contextLoads() {
		// Test to ensure the Spring application context loads successfully
	}

}
