package zzuli.zw.main.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName ConfigUtils
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 12:34
 * @Version 1.0
 */
public class ConfigUtils {
    private static Properties config;
    private static final String CONFIG_NAME = "config.properties";
    public synchronized static Properties initConfig() {
        if (null == config) {
            synchronized (ConfigUtils.class) {
                if (null == config) {
                    config = new Properties();
                    ClassLoader classLoader = ClassUtil.getClassLoader();
                    InputStream resourceAsStream = classLoader.getResourceAsStream(CONFIG_NAME);
                    try {
                        config.load(resourceAsStream);
                        return config;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }else{
                    return config;
                }
            }
        }else{
            return config;
        }
    }
    public static String getConfigAttribute(String key){
        Properties config = initConfig();
        if (config != null){
            return (String) config.get(key);
        }
        return null;
    }


    public static void main(String[] args) {
        Properties config = ConfigUtils.initConfig();
        System.out.println(config);
        String notInterceptorRequest = (String) config.get("notInterceptorRequest");
        String[] split = notInterceptorRequest.split(",");
        List<Integer> requestList = new CopyOnWriteArrayList<>();
        for (String s : split) {
            requestList.add(Integer.parseInt(s)) ;
        }
        //System.out.println(Arrays.toString(requestList.toArray()));
    }
}
