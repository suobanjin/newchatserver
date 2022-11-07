package zzuli.zw.main.utils;

import zzuli.zw.main.annotation.Interceptor;
import zzuli.zw.main.interfaces.HandlerInterceptor;
import zzuli.zw.main.utils.ClassUtil;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class InterceptorUtils {
    public synchronized static ArrayBlockingQueue<HandlerInterceptor> getInterceptors(){
        String packageName = "";
        File root = new File(System.getProperty("user.dir") + "\\src");
        ArrayBlockingQueue<Class<?>> loop = loop(root, packageName, new ArrayBlockingQueue<>(16,true));
        ArrayBlockingQueue<HandlerInterceptor> arrayBlockingQueue = new ArrayBlockingQueue<>(16,true);
        for (Class<?> aClass : loop) {
            HandlerInterceptor o = ClassUtil.newObject(aClass);
            arrayBlockingQueue.add(o);
        }
        return arrayBlockingQueue;
    }
    public static ArrayBlockingQueue<Class<?>> loop(File folder, String packageName, ArrayBlockingQueue<Class<?>> queue) {
        File[] files = folder.listFiles();
        for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
            File file = files[fileIndex];
            if (file.isDirectory()) {
                loop(file, packageName + file.getName() + ".", queue);
            } else {
                addToQueue(file.getName(), packageName, queue);
            }
        }
        return queue;
    }

    public static void addToQueue(String filename, String packageName, Queue<Class<?>> queue) {
        try {
            String name = filename.substring(0, filename.length() - 5);
            name = (packageName + name).substring(10);
            if (!name.contains("java.test") && !name.contains("java.resources")) {
                Class obj = Class.forName(name);

                if (obj.getAnnotation(Interceptor.class) != null) {

                    queue.add(Class.forName(name));
                }
            }
        } catch (Exception e) {
        }
    }
}
