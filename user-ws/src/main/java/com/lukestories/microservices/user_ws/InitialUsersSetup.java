package com.lukestories.microservices.user_ws;

import com.lukestories.microservices.user_ws.web.model.AuthorityEntity;
import com.lukestories.microservices.user_ws.web.model.RoleEntity;
import com.lukestories.microservices.user_ws.web.model.UserEntity;
import com.lukestories.microservices.user_ws.web.repository.AuthorityRepository;
import com.lukestories.microservices.user_ws.web.repository.RoleRepository;
import com.lukestories.microservices.user_ws.web.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component @Slf4j
public class InitialUsersSetup {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private AuthorityRepository authorityRepository;
    @Autowired private BCryptPasswordEncoder encoder;

    @Transactional
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Application has started - APPLICATION_READY_EVENT");

        try {
            AuthorityEntity read = createAuthority("READ");
            AuthorityEntity write = createAuthority("WRITE");
            AuthorityEntity delete = createAuthority("DELETE");

            RoleEntity roleAdmin = createRole("ROLE_ADMIN", Arrays.asList(read, write, delete));

            createUser("bob-green", "heslo", roleAdmin);
        } catch (RuntimeException e) {
            log.info("ERR MSG: transaction rollbacked: " + e.getMessage());
        }
    }

    @Transactional
    private UserEntity createUser(String username, String password, RoleEntity... roles) {
        userRepository.findByUsername(username).ifPresent(p -> { throw new RuntimeException("ALREADY_PRESENT"); });
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setEncryptedPassword(encoder.encode(password));
        userEntity.setRoles(Arrays.asList(roles));
        return userRepository.save(userEntity);
    }

    @Transactional
    private AuthorityEntity createAuthority(String command) {
        authorityRepository.findByName(command).ifPresent(p -> { throw new RuntimeException("ALREADY_PRESENT"); } );
        AuthorityEntity authority = new AuthorityEntity();
        authority.setName(command);
        return authorityRepository.save(authority);
    }
    @Transactional
    private RoleEntity createRole(String command, Collection<AuthorityEntity> authorityEntities) {
        roleRepository.findByName(command).ifPresent(p -> { throw new RuntimeException("ALREADY_PRESENT"); } );
        RoleEntity o = new RoleEntity();
        o.setName(command);
        o.setAuthorities(authorityEntities);
        return roleRepository.save(o);
    }


}
