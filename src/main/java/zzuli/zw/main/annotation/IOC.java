package zzuli.zw.main.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 定义IOC方法（类似SpringBoot的@Bean）
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IOC {
    String value() default "";
}
