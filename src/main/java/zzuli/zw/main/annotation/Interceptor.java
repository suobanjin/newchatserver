package zzuli.zw.main.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author 索半斤
 * @Description 拦截器注解
 * @Date 2022/1/27 20:55
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptor {
    int order() default -1;
    int value() default -1;
}
