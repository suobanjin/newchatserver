package zzuli.zw.main.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ClassUtil {
    private static Logger logger = LoggerFactory.getLogger(ClassUtil.class);
    private static final String FILE_PROTOCOL = "file";
    /**
     * @MethodName:  extractPackageClass
     * @date: 2020/11/20 17:39
     * @author 索半斤
     * @Description: 扫描指定包下面的类
     */
    public static Set<Class<?>> extractPackageClass(String packageName){
        //1、获取类加载器
        ClassLoader classLoader = getClassLoader();
        //2、通过类加载器获取到加载的资源
        packageName = packageName.replace(".", "/");
        URL url = classLoader.getResource(packageName);
        if (url == null) {
           logger.warn("unable to retrieve anything from package:"+packageName);
           return null;
        }
        //3、依据不同的资源类型，采用不同的方式获取资源集合
        Set<Class<?>> classSet = null;
        String protocol = url.getProtocol();
        if (protocol.equalsIgnoreCase(FILE_PROTOCOL)){
            classSet = new HashSet<>();
            try {
                //System.out.println(url.toURI().getPath());
                File packageDirectory = new File(url.toURI().getPath());
                extractClassFile(classSet, packageDirectory, packageName);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return classSet;
    }

    /**
     * @MethodName:  extractClassFile
     * @date: 2020/11/20 18:42
     * @author 索半斤
     * @Description: 获取package包下的所有的class文件
     */
    private static void extractClassFile(Set<Class<?>> emptyClassSet, File fileSource, String packageName) {
        if (fileSource.isFile()){
            return;
        }
        File[] files = fileSource.listFiles((file) -> {
            if (file.isDirectory()) {
                return true;
            } else {
                String absolutePath = file.getAbsolutePath();
                if (absolutePath.endsWith("class")) {
                    //若是class文件，则直接加载
                    Class<?> aClass = addToClassSet(absolutePath, packageName);
                    emptyClassSet.add(aClass);
                }
            }
            return false;
        });
        if (files != null) {
            for (File file : files) {
                extractClassFile(emptyClassSet, file, packageName);
            }
        }
    }

    private static Class<?> addToClassSet(String absolutePath,String packageName)  {
        //1.从class文件的绝对值路径中提取出包含了package的类名
        absolutePath = absolutePath.replace(File.separator, ".");
        absolutePath = absolutePath.substring(absolutePath.indexOf(packageName.replace("/", ".")));
        String className = absolutePath.substring(0,absolutePath.indexOf(".class"));
        //2.通过反射机制获取对应的Class对象并加入到classSet里
        return loadClass(className);
    }

    public static Class<?> loadClass(String className){
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error("error to load the class from className:"+className);
            throw new RuntimeException();
        }
    }

    /**
     * @MethodName:  getClassLoader
     * @date: 2020/11/20 17:42
     * @author 索半斤
     * @Description: 获取类加载器,坑：在test包下如果同名，获取的是test下的包
     */
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    @SuppressWarnings("unchecked")
    public static <T> T  newObject(Class<?> clazz){
        try {
            return (T)clazz.newInstance();
        }catch (Exception e){
            logger.error("class instance fail");
            throw new RuntimeException(e);
        }
    }

    /**
     * @MethodName: setField
     * @date: 2020/11/22 19:15
     * @author 索半斤
     * @Description: 为成员变量设置值
     */
    public static void setField(Field field,Object target, Object value, boolean accessible){
        field.setAccessible(accessible);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            logger.warn("the field set fail");
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Set<Class<?>> basePacket = ClassUtil.extractPackageClass("zzuli.zw");
        for (Class aClass : basePacket) {
            if (aClass.isInterface())continue;
            /*if (aClass == HandlerMethodArgumentResolver.class){

                System.out.println(aClass.newInstance());
            }*/
            /*if (aClass.getAnnotation() != null){

            }*/
        }
        //System.out.println(System.getProperty("user.dir") + "\\src");
       /* File file = new File(System.getProperty("user.dir") + "\\src");
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isDirectory()){
                System.out.println(file1);
            }
        }
        System.out.println(basePacket.size());*/
    }
}
