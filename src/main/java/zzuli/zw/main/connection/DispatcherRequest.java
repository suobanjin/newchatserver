package zzuli.zw.main.connection;

import zzuli.zw.main.annotation.RequestMapping;
import zzuli.zw.main.baseRequest.BaseRequest;
import zzuli.zw.main.exception.ServerException;
import zzuli.zw.main.interfaces.HandlerMethodArgumentResolver;
import zzuli.zw.main.factory.ArgumentResolvers;
import zzuli.zw.main.factory.RequestBeanContainer;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseCode;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.model.ResponseParameter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author 索半斤
 * @description 统一处理请求
 * @date 2022/1/22
 * @className DispatcherRequest
 */
public class DispatcherRequest extends BaseRequest {
    @Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws Exception {
        if (RequestBeanContainer.getInstance().size() == 0){
            responseError(response);
            throw new ServerException("RequestBeans don`t init...");
        }
        String url = request.getUrl();
        if (url == null || url.length() == 0){
            int requestMapping = request.getRequest();
            RequestBeanContainer.getInstance().forEach((key,value) -> {
                Method[] methods = value.getClass().getMethods();
                for (Method method : methods) {
                    if (method.getAnnotation(RequestMapping.class) == null)continue;
                    if (method.getAnnotation(RequestMapping.class).value() == requestMapping) {
                        processParameters(method,request,response,value);
                        return;
                    }else if (method.getAnnotation(RequestMapping.class).request() == requestMapping){
                        processParameters(method,request,response,value);
                        return;
                    }
                }
            });
        }else {
            Object baseRequest = RequestBeanContainer.getRequest(url);
            if (baseRequest != null) {
                int requestMapping = request.getRequest();
                Method[] methods = baseRequest.getClass().getMethods();
                for (Method method : methods) {
                    if (method.getAnnotation(RequestMapping.class) == null)continue;
                    if (method.getAnnotation(RequestMapping.class).value() == requestMapping) {
                        processParameters(method,request,response,baseRequest);
                        return;
                    }else if (method.getAnnotation(RequestMapping.class).request() == requestMapping){
                        processParameters(method,request,response,baseRequest);
                        return;
                    }
                }
            }else{
                responseError(response);
            }
        }

    }

    /**
     * @Author 索半斤
     * @Description 错误响应
     * @Date 2022/1/26 23:07
     * @Param [response]
     * @return void
     **/
    private void responseError(ResponseParameter response) throws IOException {
        ResponseMessage responseMessage = new ResponseMessage();
        //responseMessage.setRequest(RequestType.SERVER_ERROR);
        responseMessage.setCode(ResponseCode.SERVER_ERROR);
        response.write(responseMessage);
    }
    /**
     * @Author 索半斤
     * @Description 处理参数，通过反射执行方法
     * @Date 2022/1/26 23:07
     * @Param [method, request, response, value]
     * @return void
     **/
    private void processParameters(Method method, RequestParameter request, ResponseParameter response, Object value){
        try {
            int parameterCount = method.getParameterCount();
            Object[] objects = new Object[parameterCount];
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i< parameterCount;i++ ) {
                Parameter parameter = parameters[i];
                HandlerMethodArgumentResolver argumentResolver = ArgumentResolvers.getInstance().getArgumentResolverCache(parameter.getType());
                if (argumentResolver != null){
                    Object o = argumentResolver.resolveArgument(parameter,request,response,request.getResult());
                    objects[i] = o;
                }else {
                    for (HandlerMethodArgumentResolver resolver : ArgumentResolvers.getInstance()) {
                        if (resolver.supportsParameter(parameter)) {
                            ArgumentResolvers.getInstance().addArgumentResolverCache(parameter.getType(),resolver);
                            Object o = resolver.resolveArgument(parameter, request, response, request.getResult());
                            objects[i] = o;
                            break;
                        }
                    }
                }
            }

            Object responseMessage = method.invoke(value,objects);
            if (responseMessage != null) {
                response.write((ResponseMessage)responseMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
