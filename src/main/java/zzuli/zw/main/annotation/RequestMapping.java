package zzuli.zw.main.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author 索半斤
 * @Description 请求映射，标注在方法上
 * @Date 2022/1/28 13:36
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    int request() default -1;
    int value() default -1;
}
