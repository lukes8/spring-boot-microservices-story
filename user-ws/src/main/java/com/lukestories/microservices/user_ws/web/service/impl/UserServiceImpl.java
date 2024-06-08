package com.lukestories.microservices.user_ws.web.service.impl;

import com.lukestories.microservices.user_ws.web.model.User;
import com.lukestories.microservices.user_ws.web.model.dto.UserDto;
import com.lukestories.microservices.user_ws.web.repository.UserRepository;
import com.lukestories.microservices.user_ws.web.service.UserService;
import com.lukestories.microservices.user_ws.web.util.UserUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service @Slf4j
public class UserServiceImpl implements UserService {

    private static final String ERROR_CODE_NOT_FOUND = "ERROR_CODE_NOT_FOUND";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @PersistenceContext
    private EntityManager entityManager;

    public UserDto get(Long id) throws Exception {

        User user = userRepository.findById(id).orElseThrow(() -> new Exception(ERROR_CODE_NOT_FOUND));
        UserDto build = modelMapper.map(user, UserDto.class);//
//        UserDto build = UserDto.builder().id(id).password("test").username("luke").build();
        return build;
    }

    @Transactional
    public UserDto signUp(UserDto user) {

        user.setPassword(encoder.encode(user.getPassword()));
        User map = modelMapper.map(user, User.class);
        map.setLastLoginDateTime(LocalDateTime.now());
        map.setId(null);
        User saved = userRepository.save(map);
        UserDto returned = modelMapper.map(saved, UserDto.class);
        returned.setPassword(null);
        return returned;
    }

    @Transactional
    public void saveUsersInBatch() {
        Set lst = UserUtil.getSet();
        userRepository.saveAll(lst);
    }

    @Transactional
    public void saveUsersInBatchOnSecondTry() {
        List lst = UserUtil.getList();
        int batchSize = 20;
        for (int i = 0; i < lst.size(); i++) {
            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
            if (!userRepository.existsById((long) i)) {
                entityManager.persist(lst.get(i));
            } else {
                entityManager.merge(lst.get(i));
            }
        }
        entityManager.flush();
        entityManager.clear();

    }

    @Transactional(readOnly = true)
    public void processUsersInBatchLoopPaginated() {
        int batchSize = 20; // Adjust batch size as needed
        int pageNumber = 0;

        List<User> users;
        do {
            val page = PageRequest.of(pageNumber, batchSize);
            users = userRepository.findAll(page).getContent();
            for (User u : users) {
                //process
                entityManager.detach(u);
            }
            pageNumber++;
            entityManager.clear();
        } while (!users.isEmpty());
    }

    @Transactional
    public void processUsersInBatchViaStream() {
        int batchSize = 20;
        int count = 0;

        try (Stream<User> userStream = userRepository.streamAll()) {
            for (User user : (Iterable<User>)userStream::iterator) {
                //process
                entityManager.detach(user);
                count++;
                if (count % batchSize == 0) {
                    //we dont need to call flush() in case we dont modify database data and hence no sync with database is needed for persistence context
                    entityManager.clear();
                }
            }
            entityManager.clear();
        }
    }

    public void downloadUsers() throws IOException {
        //UserUtil.getList()
        String path = "path1/path2/file.txt";
        Files.createDirectories(Path.of(path).getParent());
        final List<String> collect = UserUtil.getList().stream().map(m -> m.getId().toString()).collect(Collectors.toList());
        final String join = String.join(", ", collect);
        Files.write(Path.of(path), Arrays.asList(join));



    }

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("loadUserByUsername username {}", username);
        User user = userRepository.findByUsername(username).get();
        user.setEncryptedPassword(encoder.encode("test"));
        log.debug("user username {} ", user.getUsername());
        log.debug("user password: {}", user.getEncryptedPassword());
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(username)
//                .password(user.getEncryptedPassword()).build();
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getEncryptedPassword(),
                true, true, true, true, new ArrayList<>());
    }
}