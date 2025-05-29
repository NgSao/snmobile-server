package com.snd.server.constant;

import java.util.Arrays;
import java.util.List;

public class CorsConstant {
    public static final List<String> LOCALHOST_FRONTEND = Arrays.asList("http://localhost:8080",
            "http://192.168.1.111:8080", "http://192.168.1.111:8081", "http://localhost:5173",
            "http://192.168.1.111:5173",
            "https://6d78-2405-4803-c634-1930-f4d6-9239-b7e5-3e3f.ngrok-free.app/");

    public static final List<String> ALLOWED_METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE");

    public static final List<String> ALLOWED_HEADERS = Arrays.asList("Authorization", "Cache-Control", "Content-Type");
}