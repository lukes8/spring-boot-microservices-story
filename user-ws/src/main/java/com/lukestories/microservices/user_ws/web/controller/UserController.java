package com.lukestories.microservices.user_ws.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lukestories.microservices.user_ws.web.model.dto.UserDto;
import com.lukestories.microservices.user_ws.web.service.impl.UserServiceImpl;
import com.lukestories.microservices.user_ws.web.util.FileUtil;
import com.lukestories.microservices.user_ws.web.util.UserUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@RestController
@RequestMapping("/rest/api/users") @Slf4j
public class UserController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired private Environment environment;

    @GetMapping("/status/check")
    public String status() {
        String portNbr = environment.getProperty("local.server.port");
        return "[check] running on port " + portNbr;
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) throws Exception {
        UserDto user = userService.get(userId);
        return user;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto signUp(@RequestBody UserDto user) {
        UserDto userDto = userService.signUp(user);
        return userDto;
    }

    private Path path = Path.of("path1/path2/file.txt");
    @GetMapping("/file")
    public ResponseEntity<String> getFile() {

        try {
            final String s = FileUtil.readFromFile(path);
            return ResponseEntity.ok(s);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("failed green");
        }
    }

    @GetMapping("/filejson")
    public ResponseEntity<String> getFileJson() {

        try {
            final String s = new ObjectMapper().writeValueAsString(UserUtil.getList());
            return ResponseEntity.ok(s);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("failed green");
        }
    }

    @GetMapping("/fileinputstream")
    public ResponseEntity<InputStreamResource> getFileAsInputStreamResource() {

        try {
            new ObjectMapper().writeValue(path.toFile(), UserUtil.getList());
            final InputStreamResource isr = new InputStreamResource(new FileInputStream(path.toFile()));
            return ResponseEntity.ok(isr);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    private final String processed = "processed in batch";

    @PostMapping("/batch")
    public String batching() {

        userService.saveUsersInBatch();
        return processed;
    }

    @PostMapping("/batch2")
    public String batching2() {

        userService.saveUsersInBatchOnSecondTry();
        return processed;
    }

    @PostMapping("batchWithPaginatedRows")
    public String batchWithPaginatedRows() {
        userService.processUsersInBatchLoopPaginated();
        return processed;
    }

    @PostMapping("batchWithStreamedRows")
    public String batchWithStreamedRows() {
        userService.processUsersInBatchViaStream();
        return processed;
    }
}
