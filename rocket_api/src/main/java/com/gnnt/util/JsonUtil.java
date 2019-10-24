package com.gnnt.util;

import com.alibaba.fastjson.JSONObject;

public class JsonUtil {
    private static JSONObject jsonObject = new JSONObject();

    /**
     * 将实体类转换为json, 此json串 对应响应的key
     * @param entity 实体类
     * @param key json串对应的key
     * @return json字符串
     */
    public static String parseObject2Json( Object entity, String key ){
        jsonObject.put(key,entity);
        return jsonObject.toJSONString();
    }

    /**
     * 将json串转换为指定的实体
     * @param cla 实体的class
     * @param key key
     * @param json json串
     * @return
     */
    public static Object parseJson2Object(Class cla, String key, String json){
       jsonObject = JSONObject.parseObject(json);
       String jsonObjectString = jsonObject.getString(key);
       return JSONObject.parseObject(jsonObjectString, cla);
    }
}
