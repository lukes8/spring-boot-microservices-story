package com.lukestories.microservices.order_ws;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class BobStoryWithStreamsTest {

    @Test
    void getSummaryOfBobFriendsAccordingGenderOrNames() throws IOException {

        System.out.println("Show me all Bob woman friends and total number of duplicity names for every friend");
        Path path = Paths.get("src/test/java/com/lukestories/microservices/order_ws/data", "bob-friends.txt");
        System.out.println(path.toUri());
        try (Stream<String> friends = Files.lines(path)) {
            friends.skip(1)
                    .map(String::toLowerCase)
                    .map(s -> s.split(","))
                    .filter(f -> f.length == 2)
                    .filter(f -> f[1].equals("w"))
                    .collect(Collectors.toMap(
                            arr -> arr[0],
                            arr -> 1,
                            (firstValue, secondValue) -> firstValue + 1
                    )).forEach((k, v) -> System.out.println(String.format("[%s] = %s", k, v)));
        }

        System.out.println("Show me total number of Bob's friends according gender");
        try (Stream<String> friends = Files.lines(path)) {
            friends.skip(1)
                    .map(m -> m.split(","))
                    .filter(f -> f.length == 2)
                    .collect(Collectors.groupingBy(c -> c[1], Collectors.toList()))
                    .forEach((k, arr) -> System.out.println(k + ": " + arr.size()));
        }


    }

//    private static void log(String groupName, List<String[]> groupValues) {
//        System.out.println(String.format("group[%s]: %s", groupName, ));
//
//    }

    public static void log(String key, String value) {
        System.out.println(String.format("[%s] = %s", key, value));
    }
}
