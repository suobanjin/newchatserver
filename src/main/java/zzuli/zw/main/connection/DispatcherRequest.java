package zzuli.zw.main.connection;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import zzuli.zw.config.Router;
import zzuli.zw.main.baseRequest.BaseRequest;
import zzuli.zw.main.exception.ServerException;
import zzuli.zw.main.interfaces.HandlerMethodArgumentResolver;
import zzuli.zw.main.factory.ArgumentResolvers;
import zzuli.zw.main.model.*;
import zzuli.zw.main.model.protocol.ResponseMessage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 索半斤
 * @description 统一处理请求
 * @date 2022/1/22
 * @className DispatcherRequest
 */
@Slf4j
public class DispatcherRequest extends BaseRequest {
    @Override
    public void doRequest(RequestParameter request, ResponseParameter response) throws Exception {
        if (HandlerMappingRegistry.isEmpty()){
            responseError(response);
            request.closeSocket();
            throw new ServerException("RequestBeans don`t init...");
        }
        String url = request.getUrl();
        HandlerMethod handler;
        if (!StringUtils.isEmpty(url)) {
            handler = HandlerMappingRegistry.getHandlerByPath(url);
        } else {
            handler = HandlerMappingRegistry.getHandler(request.getRequest());
        }
        if (Objects.isNull(handler)) {
            log.warn("No handler found for request: url={}, code={}", url, request.getRequest());
            responseError(response);
            return;
        }
        // 执行参数绑定 + 方法调用
        processParameters(handler.getMethod(), request, response, handler.getRequest());
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
    private void processParameters2(Method method, RequestParameter request, ResponseParameter response, Object value)  {
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
                if (responseMessage instanceof ResponseMessage) {
                    response.write((ResponseMessage) responseMessage);
                }else {
                    ResponseMessage responseMess = new ResponseMessage();
                    responseMess.setRequest(request.getRequest());
                    responseMess.setContentObject(responseMessage);
                    responseMess.setCode(ResponseCode.SUCCESS);
                    response.write(responseMess);
                }
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

    /**
     * 重构后的参数处理方法
     */
    private void processParameters(Method method, RequestParameter request, ResponseParameter response, Object beanInstance) {
        try {
            // 获取缓存的参数解析器信息，如果没有则初始化并缓存
            HandlerMethodParameterInfo methodInfo = HandlerMethodCache.getInstance().get(method);
            if (methodInfo == null) {
                methodInfo = new HandlerMethodParameterInfo(method);
                HandlerMethodCache.getInstance().put(method, methodInfo);
            }

            // 解析方法参数
            Object[] args;
            try {
                args = methodInfo.resolveArguments(request, response);
            } catch (Exception e) {
                writeErrorResponse(response, request.getRequest(), null, e);
                return;
            }

            // 调用方法
            Object result;
            try {
                result = method.invoke(beanInstance, args);
            } catch (InvocationTargetException ite) {
                // 方法内部异常
                writeErrorResponse(response, request.getRequest(), null, ite.getTargetException());
                return;
            } catch (Exception e) {
                writeErrorResponse(response, Router.ILLEGAL_REQUEST, null, e);
                return;
            }

            // 写入响应
            writeResponse(response, request, result);

        } catch (Exception e) {
            writeErrorResponse(response, Router.ILLEGAL_REQUEST, null, e);
        }
    }

    /**
     * 单例缓存类：存储 Method -> HandlerMethodParameterInfo 映射
     */
    private static class HandlerMethodCache {

        private static final HandlerMethodCache INSTANCE = new HandlerMethodCache();

        // 缓存：Method -> 参数解析信息
        private final Map<Method, HandlerMethodParameterInfo> cache = new ConcurrentHashMap<>();

        private HandlerMethodCache() {}

        public static HandlerMethodCache getInstance() {
            return INSTANCE;
        }

        public HandlerMethodParameterInfo get(Method method) {
            return cache.get(method);
        }

        public void put(Method method, HandlerMethodParameterInfo info) {
            cache.put(method, info);
        }
    }

    /**
     * 参数解析信息类
     */
    private static class HandlerMethodParameterInfo {
        private final Method method;
        private final HandlerMethodArgumentResolver[] resolvers;
        private final Parameter[] parameters;

        public HandlerMethodParameterInfo(Method method) {
            this.method = method;
            this.parameters = method.getParameters();
            this.resolvers = Arrays.stream(parameters)
                    .map(p -> {
                        HandlerMethodArgumentResolver resolver = ArgumentResolvers.getInstance().getArgumentResolverCache(p.getType());
                        if (resolver != null) return resolver;
                        for (HandlerMethodArgumentResolver r : ArgumentResolvers.getInstance()) {
                            if (r.supportsParameter(p)) {
                                ArgumentResolvers.getInstance().addArgumentResolverCache(p.getType(), r);
                                return r;
                            }
                        }
                        throw new IllegalArgumentException("No argument resolver for parameter type: " + p.getType());
                    })
                    .toArray(HandlerMethodArgumentResolver[]::new);
        }

        public Object[] resolveArguments(RequestParameter request, ResponseParameter response) throws Exception {
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                args[i] = resolvers[i].resolveArgument(parameters[i], request, response, request.getResult());
            }
            return args;
        }
    }
    /**
     * 响应写入方法
     */
    private void writeResponse(ResponseParameter response, RequestParameter request, Object result) {
        ResponseMessage msg;
        if (result instanceof ResponseMessage) {
            msg = (ResponseMessage) result;
        } else {
            msg = new ResponseMessage();
            msg.setRequest(request.getRequest());
            msg.setContentObject(result);
            msg.setCode(ResponseCode.SUCCESS);
        }
        try {
            response.write(msg);
        } catch (Exception e) {
            log.error("Failed to write response", e);
        }
    }

    /**
     * 错误响应写入方法
     */
    private void writeErrorResponse(ResponseParameter response, int requestCode, ResponseCode code, Throwable t) {
        ResponseMessage msg = new ResponseMessage();
        msg.setRequest(requestCode);
        msg.setCode(ResponseCode.SERVER_ERROR);
        try {
            response.write(msg);
        } catch (Exception ex) {
            log.error("Failed to write error response", ex);
        }
        if (t != null) log.error("Request processing error", t);
    }
}
