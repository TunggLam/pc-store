package com.example.pcstore.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaskingUtils {

    private MaskingUtils(){
        throw new IllegalArgumentException("MaskingUtils");
    }

    public static final String JAVA_LANG = "java.lang";

    public static final String FULL_MASKING = "*******";

    public static final int DISPLAY_LIST_MAX_LENGTH = 10;

    private static final List<String> MASKING_KEY = List.of("roles", "sessionId");

    private static final List<String> ENCRYPT_KEY = List.of("password", "encrypt", "secret", "token", "authorization", "accessToken");

    private static final List<String> EXCLUDE_KEY = List.of("username", "contractNumber", "forwarded", "fileName", "className", "methodName");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Object maskingSensitiveData(Object o, boolean isMasking) {
        if (o == null) {
            return o;
        }
        try {
            if (o instanceof URI) o = o.toString();
            Class<?> clazz = o.getClass();
            /*
            Nếu object không phải là dữ liệu dạng wrapper class như Integer, String, Float ...
            thì sẽ kiểm tra thông tin để trả về dữ liệu, dữ liệu mã hóa do isMasking
            */
            if (!clazz.getName().contains(JAVA_LANG)) {
                if (o instanceof Collection<?>) {
                    return maskingListData(o, isMasking);
                }
                return maskingMapData(o);
            } else {
                /*
                * Nếu trường hợp dữ liệu của map và list là 1 json map hoặc json object
                thì sẽ truy cập sâu vào các dữ liệu bên trong để mã hóa dữ liệu
                * */
                Object data = extractDataFromJsonString(o, isMasking);
                if (data != null) return data;
                if (isMasking) {
                    return FULL_MASKING;
                }
                return o.toString();
            }
        } catch (Exception e) {
            // nothing
        }
        return o;
    }

    public static Object extractDataFromJsonString(Object o, boolean isEncryptString) {
        if (!(o instanceof String value)) {
            return null;
        }
        if (value.contains("{") || value.contains("[")) {
            JsonNode jsonNode;
            try {
                jsonNode = OBJECT_MAPPER.readTree(value);
            } catch (Exception e) {
                return null;
            }
            if (jsonNode.isArray()) {
                return maskingListData(o, isEncryptString);
            } else if (jsonNode.isObject()) {
                return maskingMapData(o);
            }
        } else if (isURL(value)) {
            return maskingStringUrl(value);
        }
        return null;
    }

    public static Map<String, Object> maskingMapData(Object o) {
        Map<String, Object> map = getDataFromValue(o, new TypeReference<HashMap<String, Object>>() {
        });

        if (map == null) {
            return Collections.emptyMap();
        }

        for (Map.Entry<String, Object> value : map.entrySet()) {
            if (value.getValue() == null) {
                continue;
            }
            if (!value.getValue().getClass().getName().contains(JAVA_LANG)) {
                var maskingData = maskingSensitiveData(value.getValue(), isEncryptKey(value.getKey()));
                map.put(value.getKey(), maskingData);
                continue;
            }

            // Nếu trường hợp dữ liệu của map và list là 1 json map hoặc json object thì sẽ truy cập sâu vào các dữ liệu bên trong để mã hóa dữ liệu
            Object data = extractDataFromJsonString(value.getValue(), isEncryptKey(value.getKey()));
            if (data != null) {
                map.put(value.getKey(), data);
                continue;
            }

            if (isEncryptKey(value.getKey())) {
                map.put(value.getKey(), FULL_MASKING);
            } else if (isMaskingKey(value.getKey())) {
                String maskStr = maskingValue(value.getValue());
                map.put(value.getKey(), maskStr);
            }
        }
        return map;
    }

    public static <T> T getDataFromValue(Object o, TypeReference<T> clazz) {
        if (o instanceof String string) {
            try {
                return OBJECT_MAPPER.readValue(string, clazz);
            } catch (JsonProcessingException e) {
                return null;
            }
        } else {
            return JsonUtils.toObject(o, clazz);
        }
    }

    public static Map<String, Object> convertToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                // nothing
            }
        }
        return map;
    }

    public static boolean isMaskingKey(String key) {
        if (isExcludeKey(key)) {
            return false;
        }
        for (String keyNeedMaking : MASKING_KEY) {
            if (StringUtils.containsIgnoreCase(key, keyNeedMaking)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEncryptKey(String key) {
        if (isExcludeKey(key)) {
            return false;
        }
        for (String keyNeedEncrypt : ENCRYPT_KEY) {
            if (StringUtils.containsIgnoreCase(key, keyNeedEncrypt)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExcludeKey(String key) {
        for (String excludeKey : EXCLUDE_KEY) {
            if (StringUtils.containsIgnoreCase(key, excludeKey)) {
                return true;
            }
        }
        return false;
    }

    public static List<Object> maskingListData(Object o, boolean isMaskingString) {
        List<Object> list = getDataFromValue(o, new TypeReference<>() {
        });
        List<Object> listResult = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return listResult;
        }
        /**
         * Kiểm tra xem danh sách có nhiều hơn max length không, nếu nhiều hơn chỉ lấy từ phần tử thứ 0 đến LIST_MAX_LENGTH
         * Mục đích để tránh trả về dữ liệu quá dài
         */
        if(list.size() > DISPLAY_LIST_MAX_LENGTH){
            list = list.subList(0, DISPLAY_LIST_MAX_LENGTH);
            list.add("...");
        }
        for (Object object : list) {
            var maskingValue = maskingSensitiveData(object, isMaskingString);
            listResult.add(maskingValue);
        }
        return listResult;
    }

    public static String maskingValue(Object o) {
        String input = o.toString();
        int length = input.length() / 3;
        if (length < 1) return "*".repeat(input.length());
        return "*".repeat(length) + input.substring(length, input.length() - length) + "*".repeat(length);
    }

    public static Object[] maskingSensitiveData(Object... objects) {
        List<Object> objectList = new ArrayList<>();
        try {
            for (Object o : objects) {
                objectList.add(maskingSensitiveData(o, false));
            }
        } catch (Exception e) {
            // nothing
        }
        return objectList.toArray();
    }

    public static Object maskingSensitiveData(Object o) {
        return maskingSensitiveData(o, false);
    }

    public static Map<String, Object> setParamToMap(Object... objects) {
        Map<String, Object> objectMap = new HashMap<>();
        // Đảm bảo thứ tự nếu cùng 1 class trong logging
        Map<String, Integer> keyOrder = new HashMap<>();
        try {
            for (Object o : objects) {
                if (o != null && !o.getClass().getName().startsWith(JAVA_LANG)) {
                    String key = o.getClass().getSimpleName();
                    // nếu không có trong map thứ tự thì sẽ convert về 0
                    int order = keyOrder.get(key) == null ? 0 : keyOrder.get(key);
                    int nexOrder = order + 1;
                    keyOrder.put(key, nexOrder);
                    objectMap.put(key + nexOrder, o);
                }
            }
        } catch (Exception e) {
            // nothing
        }
        return objectMap;
    }

    public static boolean isURL(String urlStr) {
        try {
            new URL(urlStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String maskingStringUrl(String url) {
        url = url.trim();
        if (url.startsWith("http") && url.contains("?")) {
            var query = url.substring(url.indexOf("?") + 1);
            var path = "";
            var queries = query.split("&");
            for (var q : queries) {
                var k = q.substring(0, q.indexOf("="));
                var v = q.substring(q.indexOf("=") + 1);
                if (isEncryptKey(k)) {
                    v = FULL_MASKING;
                } else if (isMaskingKey(k)) {
                    v = maskingValue(v);
                }
                path = path + k + "=" + v + "&";
            }
            if (path.endsWith("&")) path = path.substring(0, path.length() - 1);
            return url.substring(0, url.indexOf("?") + 1) + path;
        }
        return url;
    }

}
