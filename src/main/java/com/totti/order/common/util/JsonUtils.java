package com.totti.order.common.util;

import com.google.gson.Gson;

public final class JsonUtils {
    private static final Gson GSON = new Gson();

    private JsonUtils() {
        // no instance
    }

    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        return GSON.fromJson(jsonStr, clazz);
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }
}
