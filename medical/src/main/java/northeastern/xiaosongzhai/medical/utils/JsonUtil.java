package northeastern.xiaosongzhai.medical.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/7 2:52
 * @Description: util for json
 */
public class JsonUtil {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Convert object to json
     * @param object object
     * @return json
     */
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    /**
     * Generic method to deserialize JSON to any type of object
     * @param json json
     * @param classOfT class
     * @return object
     * @param <T> type
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    /**
     * Generic method to deserialize JSON to any type of object with TypeToken
     * @param json json
     * @param typeOfT type
     * @return object
     * @param <T> type
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        return GSON.fromJson(json, typeOfT);
    }


}
