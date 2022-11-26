package zzuli.zw.main.argumentResolver;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.interfaces.HandlerMethodArgumentResolver;
import zzuli.zw.main.model.protocol.ResponseMessage;

import java.lang.reflect.Parameter;

/**
 * @author 索半斤
 * @description 解析RequestParameter参数
 * @date 2022/1/26
 * @className RequestParameterResolver
 */
public class RequestParameterResolver implements HandlerMethodArgumentResolver {
    public RequestParameterResolver() {
    }

    @Override
    public boolean supportsParameter(Parameter parameter) {
        Class<?> type = parameter.getType();
        return type.equals(RequestParameter.class);
    }

    @Override
    public Object resolveArgument(Parameter parameter,
                                  RequestParameter request,
                                  ResponseParameter response,
                                  ResponseMessage responseMessage) throws Exception {
        return request;
    }
}
