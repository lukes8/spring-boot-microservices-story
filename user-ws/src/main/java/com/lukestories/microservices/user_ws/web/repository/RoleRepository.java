package com.lukestories.microservices.user_ws.web.repository;

import com.lukestories.microservices.user_ws.web.model.AuthorityEntity;
import com.lukestories.microservices.user_ws.web.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);
}
