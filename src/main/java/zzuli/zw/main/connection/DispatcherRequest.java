package zzuli.zw.main.connection;

import zzuli.zw.config.Router;
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
import java.util.Arrays;
import java.util.Map;

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
            request.closeSocket();
            throw new ServerException("RequestBeans don`t init...");
        }
        String url = request.getUrl();
        if (url == null || url.length() == 0){
            int requestMapping = request.getRequest();
            boolean isEnd = false;
            for (Map.Entry<String, Object> entry : RequestBeanContainer.getInstance().entrySet()) {
                Method[] methods = entry.getValue().getClass().getMethods();
                for (Method method : methods) {
                    if (method.getAnnotation(RequestMapping.class) == null)continue;
                    if (method.getAnnotation(RequestMapping.class).value() == requestMapping) {
                        processParameters(method,request,response,entry.getValue());
                        isEnd = true;
                    }else if (method.getAnnotation(RequestMapping.class).request() == requestMapping){
                        processParameters(method,request,response,entry.getValue());
                        isEnd = true;
                    }
                }
            }
            if (!isEnd){
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setRequest(Router.ILLEGAL_REQUEST);
                responseMessage.setCode(ResponseCode.REQUEST_NOT_FOUND);
                response.write(responseMessage);
            }
        }else {
            Object baseRequest = RequestBeanContainer.getRequest(url);
            if (baseRequest != null) {
                int requestMapping = request.getRequest();
                boolean isEnd = false;
                Method[] methods = baseRequest.getClass().getMethods();
                for (Method method : methods) {
                    if (method.getAnnotation(RequestMapping.class) == null)continue;
                    if (method.getAnnotation(RequestMapping.class).value() == requestMapping) {
                        processParameters(method,request,response,baseRequest);
                        isEnd = true;
                    }else if (method.getAnnotation(RequestMapping.class).request() == requestMapping){
                        processParameters(method,request,response,baseRequest);
                        isEnd = true;
                    }
                }
                if (!isEnd){
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setRequest(Router.ILLEGAL_REQUEST);
                    responseMessage.setCode(ResponseCode.REQUEST_NOT_FOUND);
                    response.write(responseMessage);
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
        responseMessage.setRequest(Router.ILLEGAL_REQUEST);
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
    private void processParameters(Method method, RequestParameter request, ResponseParameter response, Object value)  {
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
            if (objects.length != parameters.length){
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setRequest(Router.ILLEGAL_REQUEST);
                responseMessage.setCode(ResponseCode.PARAMETER_ERROR);
                try {
                    response.write(responseMessage);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return;
            }
            Object responseMessage = method.invoke(value, objects);
            if (responseMessage != null) {
                response.write((ResponseMessage)responseMessage);
            }
        } catch (Exception e) {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setRequest(Router.ILLEGAL_REQUEST);
            responseMessage.setCode(ResponseCode.PARAMETER_ERROR);
            try {
                response.write(responseMessage);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
