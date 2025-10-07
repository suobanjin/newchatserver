package zzuli.zw.main.argumentResolver;

import cn.hutool.core.bean.BeanUtil;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.annotation.ParameterName;
import zzuli.zw.main.interfaces.HandlerMethodArgumentResolver;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.utils.ClassUtil;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 索半斤
 * @description List类型解析器
 * @date 2022/1/27
 * @className ListParameterResolver
 */
@Bean
public class ListParameterResolver implements HandlerMethodArgumentResolver {
    public ListParameterResolver() {
    }

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.getType() == List.class;
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
        Type parameterizedType = parameter.getParameterizedType();
        String typeName = parameterizedType.getTypeName();
        typeName = typeName.substring(typeName.indexOf("<")+1).replace(">","");
        Class<?> aClass = Class.forName(typeName);
        if (BeanUtil.isBean(aClass)){
            List<Map> mapList = (List<Map>)request.getParameter(name);
            List list = new ArrayList();
            for (Map map : mapList) {
                Object o = BeanUtil.fillBeanWithMap(map, ClassUtil.newObject(aClass), true, true);
                list.add(o);
            }
            return list;
        }else{
            return request.getParameter(name);
        }
    }
}
