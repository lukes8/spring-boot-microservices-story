package com.lukestories.microservices.user_ws.web.repository;

import com.lukestories.microservices.user_ws.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    @Query("SELECT u FROM mysql_story.User")
//    Stream<User> streamAll();

//    @Query("SELECT u FROM User u")
//    List<User> findAll();

    //@QueryHints(
    //        @QueryHint(name = AvailableHints.HINT_FETCH_SIZE, value = "25")
    //    )
}
