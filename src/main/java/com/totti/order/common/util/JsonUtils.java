package com.totti.order.common.util;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class JsonUtils {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateSerializer())
        .registerTypeAdapter(LocalDateTime.class, new JsonJodaDateTimeSerializer()).create();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T parseObject(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T parseObject(String json, Type typeOfT) {
        return GSON.fromJson(json, typeOfT);
    }
}

// joda datetime
class JsonJodaDateTimeSerializer implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }
        String dateStr = json.getAsString();
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        return DateUtils.parseDateTime(dateStr);
    }

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }
        return new JsonPrimitive(DateUtils.format(src));
    }
}

/**
 * gson Date序列化/反序列化
 * 
 * @author Ricky Fung
 */
class JsonDateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }
        String dateStr = json.getAsString();
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        return DateUtils.parseDate(dateStr);
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }
        return new JsonPrimitive(DateUtils.format(src));
    }
}
