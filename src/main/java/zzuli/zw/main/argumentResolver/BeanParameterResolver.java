package zzuli.zw.main.argumentResolver;

import cn.hutool.core.bean.BeanUtil;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.main.annotation.ParameterName;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.interfaces.HandlerMethodArgumentResolver;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.utils.ClassUtil;
import java.lang.reflect.Parameter;
import java.util.Map;
/**
 * @author 索半斤
 * @description pojo类型解析
 * @date 2022/1/26
 * @className BeanParameterResolver
 */
@Bean
public class BeanParameterResolver implements HandlerMethodArgumentResolver {
    public BeanParameterResolver() {
    }

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return BeanUtil.isBean(parameter.getType()) && parameter.getType().equals();
    }

    @Override
    public Object resolveArgument(Parameter parameter,
                                  RequestParameter request,
                                  ResponseParameter response,
                                  ResponseMessage responseMessage) throws Exception {
        if (parameter.getType() == request.getClass())return request;
        if (parameter.getType() == response.getClass())return response;
        ParameterName annotation = parameter.getAnnotation(ParameterName.class);
        String name;
        if (annotation == null){
            name = parameter.getType().getSimpleName();
            // name = name.substring(name.lastIndexOf(".")+1);
            // name = name.substring(0,1).toLowerCase().concat(name.substring(1));
        }else {
            name = annotation.value();
        }
        Map BeanMap = (Map)request.getParameter(name);
        if (BeanMap == null || BeanMap.size() == 0)return null;
        Object o = BeanUtil.fillBeanWithMap(
                BeanMap,
                ClassUtil.newObject(parameter.getType()),
                true,
                true
        );
        return o;
    }
}
