package zzuli.zw.main.utils;

import zzuli.zw.main.annotation.Interceptor;
import zzuli.zw.main.interfaces.HandlerInterceptor;
import zzuli.zw.main.utils.ClassUtil;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class InterceptorUtils {
    public synchronized static ArrayBlockingQueue<HandlerInterceptor> getInterceptors(){
        String packageName = "";
        File root = new File(System.getProperty("user.dir") + "\\src");
        Class<?>[] loop = loop(root, packageName, new Class<?>[10],0);
        ArrayBlockingQueue<HandlerInterceptor> arrayBlockingQueue = new ArrayBlockingQueue<>(16,true);
        System.out.println(Arrays.toString(loop));
        for (Class<?> aClass : loop) {
            if (aClass == null)break;
            HandlerInterceptor o = ClassUtil.newObject(aClass);
            arrayBlockingQueue.add(o);
        }
        return arrayBlockingQueue;
    }
    public static Class<?>[] loop(File folder, String packageName, Class<?>[] queue,int index) {
        File[] files = folder.listFiles();
        for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
            File file = files[fileIndex];
            if (file.isDirectory()) {
                loop(file, packageName + file.getName() + ".", queue,index);
            } else {
                addToQueue(file.getName(), packageName, queue,index);
            }
        }
        return queue;
    }

    public static void addToQueue(String filename, String packageName,Class<?>[] classList,int index) {
        try {
            String name = filename.substring(0, filename.length() - 5);
            name = (packageName + name).substring(10);
            if (!name.contains("java.test") && !name.contains("java.resources")) {
                Class obj = Class.forName(name);
                if (obj.getAnnotation(Interceptor.class) != null) {
                    Interceptor annotation = (Interceptor) obj.getAnnotation(Interceptor.class);
                    if (annotation.value() == -1 && annotation.order() == -1){
                        if (index == classList.length)classList = addCap(classList);
                        classList[index] = obj;
                        index++;
                    }else if (annotation.order() >= 0){
                        if (annotation.order() > classList.length-1){
                            classList = addCap(classList);
                        }
                        if (classList[annotation.order()] != null){
                            classList[annotation.order()+1] = obj;
                        }else{
                            classList[annotation.order()] = obj;
                        }
                    }else if (annotation.value() >= 0){
                        if (annotation.value() > classList.length-1){
                            classList = addCap(classList);
                        }
                        if (classList[annotation.value()] != null){
                            classList[annotation.value()+1] = obj;
                        }else{
                            classList[annotation.value()] = obj;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public static Class<?>[] addCap(Class<?>[] classes){
        Class<?>[] temp = new Class[classes.length*2];
        System.arraycopy(classes,0,temp,0,classes.length);
        return temp;
    }
}
