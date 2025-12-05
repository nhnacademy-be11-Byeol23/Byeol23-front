package com.nhnacademy.byeol23front.auth.feign;

public class TokenContext {

    private static final ThreadLocal<String> ACCESS_TOKEN = new ThreadLocal<>();

    public static void set(String token) {
        ACCESS_TOKEN.set(token);
    }

    public static String get() {
        return ACCESS_TOKEN.get();
    }

    public static void clear() {
        ACCESS_TOKEN.remove();
    }
}
