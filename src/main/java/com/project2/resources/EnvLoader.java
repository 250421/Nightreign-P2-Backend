package com.project2.resources;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Map;

public class EnvLoader {
    public static void init() {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}