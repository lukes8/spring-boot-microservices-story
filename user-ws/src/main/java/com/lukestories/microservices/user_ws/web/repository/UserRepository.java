package com.lukestories.microservices.user_ws.web.repository;

import com.lukestories.microservices.user_ws.web.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM USERS u")
    Stream<UserEntity> streamAll();


    @Query("SELECT u FROM USERS u")
    List<UserEntity> findAll();

    Optional<UserEntity> findByUsername(String username);

    //@QueryHints(
    //        @QueryHint(name = AvailableHints.HINT_FETCH_SIZE, value = "25")
    //    )
}
