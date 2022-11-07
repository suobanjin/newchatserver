package zzuli.zw.main.argumentResolver;

import zzuli.zw.main.annotation.ParameterName;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseMessage;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.interfaces.HandlerMethodArgumentResolver;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * @author 索半斤
 * @description Integer类型参数解析
 * @date 2022/1/26
 * @className IntegerParameterResolver
 */
public class SimpleParameterResolver implements HandlerMethodArgumentResolver {
    public SimpleParameterResolver() {
    }

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.getType() == Integer.class ||
               parameter.getType() == Double.class  ||
               parameter.getType() == Float.class   ||
               parameter.getType() == Long.class    ||
               parameter.getType() == String.class  ||
               parameter.getType() == Map.class     ||
               parameter.getType() == int.class     ||
               parameter.getType() == double.class ||
               parameter.getType() == float.class  ||
               parameter.getType() == long.class   ||
               parameter.getType() == char.class   ||
               parameter.getType() == byte.class   ||
               parameter.getType() == Character.class ||
               parameter.getType() == Byte.class;
    }

    @Override
    public Object resolveArgument(Parameter parameter,
                                  RequestParameter request,
                                  ResponseParameter response,
                                  ResponseMessage responseMessage) throws Exception {
        ParameterName annotation = parameter.getAnnotation(ParameterName.class);
        String name;
        if (annotation == null){
            name = parameter.getType().getName();
            name = name.substring(name.lastIndexOf(".")+1);
            name = name.substring(0,1).toLowerCase().concat(name.substring(1));
        }else {
            name = annotation.value();
        }

        return request.getParameter(name);
    }
}
