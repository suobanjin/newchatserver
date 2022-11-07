package zzuli.zw.main.annotation;

import java.lang.annotation.*;

/**
 * @Author 索半斤
 * @Description 参数注解，用来标注参数名称，默认为空字符串，若获取为空值，
 * 则默认使用参数类型的字符串作为参数名（如List参数，若参数没有标注此注解或注解值为空，
 * 则默认使用list作为参数名称值）
 * @Date 2022/1/28 13:32
 * @Param
 * @return
 **/
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParameterName {
    String value() default "";
}
