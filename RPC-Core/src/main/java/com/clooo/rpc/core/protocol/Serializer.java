package com.clooo.rpc.core.protocol;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;

public interface Serializer {

    <T> byte[] serializer(T obj);

    <T> T deserialize(Class<T> t, byte[] bytes);

    enum Algorithm implements Serializer {
        Java {
            @Override
            public <T> byte[] serializer(T obj) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(obj);
                    return bos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("序列化失败", e);
                }

            }

            @Override
            public <T> T deserialize(Class<T> tClass, byte[] bytes) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return tClass.cast(ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("反序列化失败", e);
                }

            }
        },

        Json {
            @Override
            public <T> byte[] serializer(T obj) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new CodeC()).create();
                String json = gson.toJson(obj);
                return json.getBytes();
            }

            @Override
            public <T> T deserialize(Class<T> t, byte[] bytes) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new CodeC()).create();
                String json = new String(bytes);
                return gson.fromJson(json, t);
            }

        }

    }

    class CodeC implements JsonDeserializer<Class<?>>, JsonSerializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                String className = jsonElement.getAsString();
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> aClass, Type type, JsonSerializationContext jsonSerializationContext) {
            // class -> json
            return new JsonPrimitive(aClass.getName());
        }
    }

}
