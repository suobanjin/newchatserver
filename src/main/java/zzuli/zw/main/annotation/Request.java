package zzuli.zw.main.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author 索半斤
 * @Description 用来标注请求类型，用于类上面，可以为空值
 * @Date 2022/1/28 13:35
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Request {
    String request() default "";
    String value() default "";
}
