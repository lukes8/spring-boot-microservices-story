package com.lukestories.microservices.user_ws;

import com.lukestories.microservices.user_ws.web.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SpringBootMicroservicesApplicationTests {

	@Autowired private UserService userService;

	@Test
	void contextLoads() {
	}

	@Test
	void testPositive() throws IOException {
		userService.downloadUsers();
	}

}
