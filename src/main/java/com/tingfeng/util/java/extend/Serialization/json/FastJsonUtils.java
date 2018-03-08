package com.tingfeng.util.java.extend.Serialization.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tingfeng.util.java.base.Serialization.common.JsonFieldProperty;
import com.tingfeng.util.java.base.Serialization.common.JsonType;
import com.tingfeng.util.java.base.common.constant.ObjectType;
import com.tingfeng.util.java.base.common.utils.ObjectTypeUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class FastJsonUtils {
    /**
     * 查看一个字符串是否是一[开头,即是否有可能是数组;
     */
    private static final Pattern pattern = Pattern.compile("^([\\s]{0,}\\[).*");

    /**
     * deep转换可以支持数组的数组转换等json嵌套类型
     * 
     * @param jsonString
     *            json字符串
     * @param cls
     *            需要转换的class类
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static List<?> deepParseArray(String jsonString, Class<?> cls)
            throws IllegalAccessException, IllegalArgumentException, InstantiationException {
        if (isNull(jsonString)) {
            return new ArrayList<>();
        }
        JSONArray jsonArr = JSON.parseArray(jsonString);
        List<Object> list = new ArrayList<>();
        Object o;
        for (int i = 0; i < jsonArr.size(); i++) {
            String jarString = jsonArr.getString(i);
            Matcher matcher = pattern.matcher(jarString);
            if (matcher.find()) {// 如果数组中包含的仍然是一个数组
                try {
                    List<?> tmp = deepParseArray(jarString, cls);
                    list.add(tmp);
                } catch (JSONException e) {
                    o = deepParseObject(jarString, cls);
                    list.add(o);
                }
            } else {
                o = deepParseObject(jarString, cls);
                list.add(o);
            }
        }
        return list;
    }

    public static <T> T deepParseObject(String jsonString, Class<T> cls)
            throws InstantiationException, IllegalAccessException {
        if (isNull(jsonString)) {
            return null;
        }
        T o = cls.newInstance();
        JSONObject jsonObj = (JSONObject) JSONObject.parse(jsonString);
        Field[] fields = cls.getFields();
        for (int j = 0; j < fields.length; j++) {
            Field field = fields[j];
            field.setAccessible(true);
            Annotation[] annotations = field.getAnnotations();
            // 为自定义的Annotation赋值
            JsonFieldProperty jsonFieldProperty = null;
            for (int a = 0; a < annotations.length; a++) {
                if (annotations[a] instanceof JsonFieldProperty) {
                    jsonFieldProperty = (JsonFieldProperty) annotations[a];
                    break;
                }
            }
            Object tmp = null;
            if (jsonFieldProperty == null || jsonFieldProperty.JsonType() == null
                    || jsonFieldProperty.JsonType() == JsonType.Base) {
                tmp = jsonObj.get(field.getName());
                ObjectType type = ObjectTypeUtils.getObjectType(field);
                if (tmp != null) {
                    if (type.equals(ObjectType.Boolean)) {
                        field.set(o, jsonObj.getBoolean(field.getName()));
                    } else if (type.equals(ObjectType.Date)) {
                        field.set(o, jsonObj.getDate(field.getName()));
                    } else if (type.equals(ObjectType.Integer)) {
                        field.set(o, jsonObj.getInteger(field.getName()));
                    } else if (type.equals(ObjectType.Long)) {
                        field.set(o, jsonObj.getLong(field.getName()));
                    } else if (type.equals(ObjectType.Float)) {
                        field.set(o, jsonObj.getFloat(field.getName()));
                    } else if (type.equals(ObjectType.Double)) {
                        field.set(o, jsonObj.getDouble(field.getName()));
                    } else if (type.equals(ObjectType.String)) {
                        field.set(o, jsonObj.getString(field.getName()));
                    } else {
                        field.set(o, tmp);
                    }
                }
            } else if (jsonFieldProperty.JsonType() == JsonType.JsonObject) {
                tmp = jsonObj.getString(field.getName());
                if (tmp != null)
                    field.set(o, deepParseObject(tmp.toString(), jsonFieldProperty.cls()));
            } else if (jsonFieldProperty.JsonType() == JsonType.JsonList) {
                tmp = jsonObj.getString(field.getName());
                if (tmp != null)
                    field.set(o, deepParseObject(tmp.toString(), jsonFieldProperty.cls()));
            }
        }
        return o;
    }

    public static <T> List<T> parseArray(String jsonString, Class<T> cls) {
        if (isNull(jsonString)) {
            return new ArrayList<T>();
        }
        return JSONArray.parseArray(jsonString, cls);

    }

    public static <T> T parseObject(String jsonString, Class<T> cls) {
        if (isNull(jsonString)) {
            return null;
        }
        return JSON.parseObject(jsonString, cls);
    }

    public static <T> Set<T> parseSet(String jsonString, Class<T> cls) {
        List<T> list = parseArray(jsonString, cls);
        Set<T> set = new HashSet<>();
        set.addAll(list);
        return set;
    }

    private static boolean isNull(String str) {
        return str == null || str.trim().equals("");
    }
}
