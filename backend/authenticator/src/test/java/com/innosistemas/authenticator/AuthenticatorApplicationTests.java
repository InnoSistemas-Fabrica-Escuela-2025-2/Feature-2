package com.innosistemas.authenticator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AuthenticatorApplicationTests {

	@Test
	void contextLoads() {
		/* Test vacío intencionalmente.
		Su propósito es verificar que el contexto de Spring Boot se cargue sin errores. */
	}

}
