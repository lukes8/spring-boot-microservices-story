package com.lukestories.microservices.user_ws.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lukestories.microservices.user_ws.web.model.dto.UserDto;
import com.lukestories.microservices.user_ws.web.service.UserService;
import com.lukestories.microservices.user_ws.web.util.FileUtil;
import com.lukestories.microservices.user_ws.web.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

@RestController
@RequestMapping("/rest/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
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
