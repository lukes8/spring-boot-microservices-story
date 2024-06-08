package com.lukestories.microservices.user_ws;

import com.lukestories.microservices.user_ws.web.model.dto.UserDto;
import com.lukestories.microservices.user_ws.web.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceTests {

	@Autowired private UserServiceImpl userService;

	@Test
	void contextLoads() {
	}

	@Test
	void testPositive() throws Exception {
		UserDto userDto = userService.get(136L);
        Assertions.assertNotNull(userDto);
		System.out.println(userDto);
		UserDetails luke = userService.loadUserByUsername("luke");
        Assertions.assertNotNull(luke);
		System.out.println(luke);
	}

}
