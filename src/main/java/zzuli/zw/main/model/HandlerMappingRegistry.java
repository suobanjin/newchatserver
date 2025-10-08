package zzuli.zw.main.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import zzuli.zw.main.annotation.RequestMapping;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HandlerMappingRegistry {

    // 存储映射关系：请求码 -> HandlerMethod
    private static final Map<Integer, HandlerMethod> handlerMap = new ConcurrentHashMap<>();
    // 新增：路径 -> Handler
    private static final Map<String, HandlerMethod> pathHandlerMap = new ConcurrentHashMap<>();
    /**
     * 启动时扫描所有RequestBean中的@RequestMapping方法
     */
    public static void registerHandler(Object controller) {
        Class<?> clazz = controller.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            RequestMapping mapping = method.getAnnotation(RequestMapping.class);
            if (mapping == null) continue;
            // 支持路径映射
            String path = mapping.url();
            if (!StringUtils.isEmpty(path)) {
                path = normalizePath(path);
                if (pathHandlerMap.containsKey(path)) {
                    throw new RuntimeException("Duplicate mapping path: " + path);
                }
                pathHandlerMap.put(path, new HandlerMethod(controller, method));
                continue;
            }

            // 码值映射
            int key = mapping.value() != -1 ? mapping.value() : mapping.request();
            if (handlerMap.containsKey(key)) {
                throw new RuntimeException("Duplicate @RequestMapping key: " + key + " in " + clazz.getName());
            }
            handlerMap.put(key, new HandlerMethod(controller, method));
        }
    }

    /**
     * 根据路径查找Handler
     */
    public static HandlerMethod getHandlerByPath(String path) {
        if (path == null) return null;
        path = normalizePath(path);
        return pathHandlerMap.get(path);
    }

    private static String normalizePath(String path) {
        if (!path.startsWith("/")) path = "/" + path;
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        return path.toLowerCase();
    }
    /**
     * 根据请求码查找对应的Handler
     */
    public static HandlerMethod getHandler(int requestCode) {
        return handlerMap.get(requestCode);
    }

    /**
     * 判断是否为空
     */
    public static boolean isEmpty(){
        return handlerMap.isEmpty();
    }
}
