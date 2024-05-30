package com.lukestories.microservices.user_ws.web.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FileUtil {

    public static String readFromFile(Path path) throws IOException {
        final String s = Files.readString(path);
        return s;
    }

    public static void writeToFile(Path path, String str) throws IOException {
        Files.write(path, Arrays.asList(str));
    }
}
