package com.lukestories.test;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BobStoryWithStreams {

    final Path path = Paths.get("src/test/java/com/lukestories/test/data", "bob-friends.txt");

    @Test
    void getSummaryOfBobFriendsAccordingGenderOrNames() throws IOException {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));

        System.out.println("Show me all Bob woman friends and total number of duplicity names for every friend");
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

    @Test
    void getFirstNonRepeatedCharacterFromStringViaStreams() {
        String text = "asdasgasfzgedhtjhd";
        List<String> list = Arrays.stream(text.split("")).toList();

        System.out.println("First non repeated character from string in modern way");
        Optional<Map.Entry<String, Integer>> entry = list.stream().collect(Collectors.toMap((k) -> k,
                (v) -> 1,
                (integer, integer2) -> integer + 1,
                LinkedHashMap::new)).entrySet().stream().filter(f -> f.getValue().equals(1)).findFirst();
        entry.ifPresent(System.out::println);

        System.out.println("First non repeated character from string in modern and more readable way");
        list.stream().collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting())).entrySet().stream().filter(f -> f.getValue().equals(1L)).findFirst().map(Map.Entry::getKey).ifPresent(System.out::println);
    }

    @Test
    void getSummaryOfBobFruitsGroupedByFirstCharAndSortedInDescendingOrder() {
        List<String> lst = Arrays.asList("mango", "apple", "lemon", "pineapple", "banana", "Mandarin", "avocado", "blueberry");
        Map<Character, List<String>> collect = lst.stream().collect(Collectors.groupingBy(k -> k.charAt(0)));

        System.out.println(collect);
        //TODO
    }

    @Test
    void getSummaryOfBobAverageExpansesForParticularServicesSortedInDescendingOrder() {
        List<String> services = Arrays.asList("cinema", "food", "traveling", "music", "sport", "books", "courses", "food", "traveling");
        List<Integer> expanses = Arrays.asList(12, 11, 44, 22, 14, 7, 16, 22, 34);
        Assert.isTrue(services.size() == expanses.size(), "sizes must be equal");
        Map<String, List<Integer>> collect = IntStream.range(0, services.size()).boxed().collect(Collectors.groupingBy(services::get, LinkedHashMap::new, Collectors.mapping(i -> expanses.get(i), Collectors.toList())));
        System.out.println("Summary of expanses for every service in natural flow [without aggregating summary function]");
        collect.entrySet().forEach(System.out::println);

        System.out.println("Summary of expanses for every service in aggregated flow [with aggregating summary function]");
        IntStream.range(0, services.size()).boxed().collect(Collectors.groupingBy(services::get, Collectors.averagingInt(expanses::get))).entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
                k -> k.getKey(),
                v -> v.getValue(),
                (v1, v2) -> v1,
                LinkedHashMap::new
        )).entrySet().forEach(System.out::println);
    }

    @Test
    void getSummaryOfBobDogFriends() throws IOException {

        System.out.println("Show me total number of Bob's dog friends");
        try (Stream<String> friends = Files.lines(path)) {
            friends.skip(1)
                    .map(String::toLowerCase)
                    .map(m -> m.split(","))
                    .filter(f -> f.length == 3 && f[1].equals("d"))
                    .collect(Collectors.toMap(
                            (arr) -> arr[0],
                            (arr) -> arr[2],
                            (firstValue, nextValue) -> String.format("%s, %s", firstValue, nextValue)
                    )).forEach((k, v) -> System.out.println(String.format("[%s] = %s", k, v)));
        }

        System.out.println("Show me again total number of Bob's dog friends");
        Animal polly = new Animal("polly", 21);
        Animal amy = new Animal("amy", 1);
        Map<Animal, String> map = new HashMap<>();
        map.put(polly, "category_dogs");
        map.put(amy, "category_dogs");
        map.forEach((k, v) -> {
            System.out.println("Do you know in general what Object class is going to generate with toString() and hashCode() as output?");
            System.out.println("No, show me product as key of hashMap");
            System.out.println("{CLASS NAME}@{HASH CODE AS MEMORY ADDRESS WHERE OBJECT IS LOCATED}");
            System.out.println(k);
        });

        System.out.println("Show me total number of Bob's friends");
        System.out.println("Do you know why some of duplicates are placed in one line (polly) and some not (luke)?");
        try (Stream<String> friends = Files.lines(path)) {
            friends.skip(1)
                    .map(String::toLowerCase)
                    .map(m -> m.split(","))
                    .filter(f -> f.length == 2 || f.length == 3)
                    .collect(Collectors.toMap(
                            (arr) -> new Animal(arr[0], arr.length == 3 ? Integer.parseInt(arr[2]) : -1),
                            (arr) -> arr[1],
                            (firstValue, nextValue) -> String.format("%s, %s", firstValue, nextValue)
                    )).forEach((k, v) -> System.out.println(String.format("[%s] = %s", k.getName(), v)));
        }

        System.out.println("Show me summary of Bob's friends grouped by gender and sorted by age in descending order");
        try (Stream<String> friends = Files.lines(path)) {
            friends.skip(1)
                    .map(String::toLowerCase)
                    .map(m -> {
                        String[] split = m.split(",");
                        if (split.length == 2) {
                            return new Friend(split[0], split[1], -1);
                        }
                        if (split.length == 3) {
                            return new Friend(split[0], split[1], Integer.parseInt(split[2]));
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Friend::getAge).reversed()) // only dogs are sorted
                    .collect(Collectors.groupingBy(
                            Friend::getGender, Collectors.toList()
                    ))
                    // Collection of group
                    .forEach((collectionKey, group) -> System.out.println(String.format("%s - %s", collectionKey, group)));
        }
    }

    static class Friend {
        private String name;
        private String gender;
        private Integer age;

        public Friend(String gender, String name) {
            this.gender = gender;
            this.name = name;
        }

        public Friend(String name, String gender, Integer age) {
            this.name = name;
            this.gender = gender;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public String getGender() {
            return gender;
        }

        public Integer getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "Friend{" +
                    "name='" + name + '\'' +
                    ", gender='" + gender + '\'' +
                    ", age=" + age +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Friend friend = (Friend) o;
            return Objects.equals(name, friend.name) && Objects.equals(gender, friend.gender) && Objects.equals(age, friend.age);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, gender, age);
        }
    }

    static class Animal {
        private String name;
        private Integer age;

        public Animal(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Animal animal = (Animal) o;
            return Objects.equals(name, animal.name) && Objects.equals(age, animal.age);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }
    }

    public static void log(String key, String value) {
        System.out.println(String.format("[%s] = %s", key, value));
    }
}
