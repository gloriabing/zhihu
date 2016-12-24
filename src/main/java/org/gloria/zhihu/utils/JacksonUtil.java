package org.gloria.zhihu.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Create on 2016/12/16 17:39.
 *
 * @author : gloria.
 */
public class JacksonUtil {

    private static ObjectMapper mapper = null;

    static {
        mapper = new ObjectMapper();
    }

    /**
     * json 转 对象
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * json 转 对象列表
     * @param json
     * @param <T>
     * @return
     */
    public static <T> List<T> fromJson(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<T>>(){});
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 对象 转 json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> String toJson(Class<T> clazz) {
        try {
            return mapper.writeValueAsString(clazz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * 对象集合 转 json
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> String toJson(Collection<T> collection) {
        try {
            return mapper.writeValueAsString(collection);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            return null;
        }
    }


    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://v5m.api.mgtv.com/vrs/getByPartId?partId=3734000&clipId=298442").build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            ObjectMapper mapper = new ObjectMapper();

            //jsonNode 相当于 Gson中的JsonObject
            JsonNode jsonNode = mapper.readTree(response.body().string());

            JsonNode node = jsonNode.get("data");
            System.out.println(node.get("iplimited").asBoolean());
        }
    }
}
