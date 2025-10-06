package zzuli.zw.main.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Transaction
 * @Description 事务处理，标注在方法上
 * @Author 索半斤
 * @Date 2021/1/21 20:56
 * @Version 1.0
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Transaction {
    Class<?> rollbackFor() default Exception.class;
}
