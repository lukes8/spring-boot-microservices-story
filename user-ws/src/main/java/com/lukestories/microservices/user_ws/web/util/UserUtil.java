package com.lukestories.microservices.user_ws.web.util;

import com.lukestories.microservices.user_ws.web.model.UserEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserUtil {

    public static Set getSet() {
        Set set = new HashSet();
        for (int i = 0; i < 1000; i++) {
            UserEntity user = UserEntity.builder().id((long) i).username("usergreen" + i).encryptedPassword("test" + i).build();
            set.add(user);
        }
        return set;
    }
    public static List<UserEntity> getList() {
        List<UserEntity> set = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            UserEntity user = UserEntity.builder().id((long) i).username("usergreen" + i).encryptedPassword("test" + i).build();
            set.add(user);
        }
        return set;
    }
}
