package zzuli.zw.main.utils;

import org.apache.commons.lang3.StringUtils;
import java.util.Properties;

public class PropertiesUtils {

    public static boolean isPlaceholder(String rawVal) {
        if (StringUtils.isEmpty(rawVal))return false;
        return rawVal.startsWith("${") && rawVal.endsWith("}");
    }
    public static String resolveDefaultValue(String rawVal) {
        if (StringUtils.isEmpty(rawVal))return null;
        if (rawVal.contains(":")){
            rawVal = rawVal.substring(2, rawVal.length() - 1);
            String[] split = rawVal.split(":");
            return split[1];
        }
        return null;
    }
    public static String resolveKey(String rawVal) {
        String key = rawVal.substring(2, rawVal.length() - 1);
        if (StringUtils.isEmpty(key))return null;
        if (key.contains(":"))key = key.substring(0, key.indexOf(":"));
        return key;
    }

    public static String resolvePlaceholderValue(Properties properties, String rawVal) {
        if (!PropertiesUtils.isPlaceholder(rawVal)) {
            throw new RuntimeException("不支持的表达式类型");
        }
        String key = resolveKey(rawVal);
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(properties.getProperty(key))){
            String defaultValue = resolveDefaultValue(rawVal);
            if (StringUtils.isNotEmpty(defaultValue))return defaultValue;
            throw new RuntimeException("不支持的表达式类型");
        }
        return properties.getProperty(key);
    }

    public static Object convertValue(Class<?> targetType, String value){
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        }
        if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        }
        return value;
    }
}
