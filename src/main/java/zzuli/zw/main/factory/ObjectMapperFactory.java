package zzuli.zw.main.factory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @ClassName ObjectMapperFactory
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/8 12:41
 * @Version 1.0
 */
public class ObjectMapperFactory {
    enum Singleton{
        ObjectMapper;
        private ObjectMapper objectMapper;
        Singleton(){
            objectMapper = new ObjectMapper();
            TimeZone china = TimeZone.getTimeZone("GMT+08:00");
            objectMapper.setTimeZone(china);
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        }
        public ObjectMapper getInstance(){return objectMapper;}
    }
    public static ObjectMapper getInstance(){
        return Singleton.ObjectMapper.getInstance();
    }

    public static  <T> T getObjectFromString(String content,Class clazz){
        try {
            return (T) getInstance().readValue(content,clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ObjectMapper getNewInstance(){
        return new ObjectMapper();
    }
}
