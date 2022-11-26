package zzuli.zw.main.argumentResolver;

import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.interfaces.HandlerMethodArgumentResolver;
import zzuli.zw.main.model.protocol.ResponseMessage;

import java.lang.reflect.Parameter;

/**
 * @author 索半斤
 * @description 解析ResponseParameter参数
 * @date 2022/1/26
 * @className ResponseParameterResolver
 */
public class ResponseParameterResolver implements HandlerMethodArgumentResolver {
    public ResponseParameterResolver() {
    }

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.getType() == ResponseParameter.class;
    }

    @Override
    public Object resolveArgument(Parameter parameter,
                                  RequestParameter request,
                                  ResponseParameter response,
                                  ResponseMessage responseMessage) throws Exception {
        return response;
    }
}
