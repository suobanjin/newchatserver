package zzuli.zw.main.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import zzuli.zw.config.Router;
import zzuli.zw.main.factory.InterceptorsQueue;
import zzuli.zw.main.factory.SessionContainer;
import zzuli.zw.main.interfaces.HandlerInterceptor;
import zzuli.zw.main.factory.ObjectMapperFactory;
import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseCode;
import zzuli.zw.main.model.protocol.ResponseMessage;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.utils.ConfigUtils;
import zzuli.zw.main.utils.ProtocolUtils;
import zzuli.zw.main.utils.SocketUtils;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName ServerThread
 * @Description 服务器端处理用户请求的线程
 * @Author 索半斤
 * @Date 2021/1/18 16:10
 * @Version 2.0
 */
public class RequestServerThread implements Runnable {
    private Socket socket;
    private static final String attribute = "notInterceptorRequest";
    private static final List<Integer> list = new CopyOnWriteArrayList<>();
    private AtomicBoolean flag = new AtomicBoolean(true);
    private ServerContext serverContext;

    public RequestServerThread(Socket socket, ServerContext serverContext) {
        this.socket = socket;
        this.serverContext = serverContext;
    }

    //关闭当前线程
    public void close() {
        this.flag.set(false);
    }

    @Override
    public void run() {
        while (true) {
            if (socket.isClosed()) break;
            if (!flag.get()) {
                SocketUtils.closeSocket(socket);
                break;
            }
            ResponseMessage result = null;
            try {
                result = ProtocolUtils.receive(socket);
                if (result == null)continue;
                boolean isContinue = handlerRequest(result);
                if (!isContinue) {
                }
            } catch (Exception e) {
                if (result == null){
                    ResponseMessage response = new ResponseMessage();
                    response.setRequest(Router.ILLEGAL_REQUEST);
                    response.setCode(ResponseCode.REQUEST_ERROR);
                    try {
                        ProtocolUtils.send(response,socket);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }else {
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setRequest(Router.ILLEGAL_REQUEST);
                    responseMessage.setCode(ResponseCode.SERVER_ERROR);
                    try {
                        ProtocolUtils.send(responseMessage,socket);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        }
    }


    public synchronized boolean handlerRequest(ResponseMessage result) throws Exception {
        DispatcherRequest dispatcherRequest = new DispatcherRequest();
        RequestParameter requestParameter = initRequestParameter(result);
        if (requestParameter == null) return false;
        ResponseParameter responseParameter = initResponseParameter();
        if (list.size() == 0) {
            initNotInterceptorBeans();
        }
        if (list.size() == 0 || !list.contains(requestParameter.getRequest())) { //配置文件中没有配置放行的请求
            ArrayBlockingQueue<HandlerInterceptor> interceptors = InterceptorsQueue.getInstance();
            int i = 0;
            boolean isContinue = true;
            try {
                if (interceptors.size() != 0) { //配置了拦截器则依次执行配置的前置拦截方法
                    for (HandlerInterceptor interceptor : interceptors) {
                        boolean b = interceptor.preHandle(requestParameter, responseParameter, null);
                        i++;
                        isContinue = b;
                        if (!b) break;
                    }
                }
                if (isContinue) {
                    dispatcherRequest.doRequest(requestParameter, responseParameter);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (i != 0) { // 执行后置拦截
                    for (int j = 0; j < i; j++) {
                        interceptors.peek().afterCompletion(requestParameter, responseParameter, null, e);
                    }
                }
            } finally {
                if (i != 0) {
                    for (int j = 0; j < i; j++) { // 执行最终拦截
                        interceptors.peek().postHandle(requestParameter, responseParameter, null);
                    }
                }
            }
        } else {
            dispatcherRequest.doRequest(requestParameter, responseParameter); //没有配置拦截器则直接执行请求
        }
        return true;
    }

    /**
     * 对请求参数进行封装
     *
     * @param responseMessage
     * @return RequestParameter
     */
    @SuppressWarnings("unchecked")
    private RequestParameter initRequestParameter(ResponseMessage responseMessage) {
        RequestParameter requestParameter = new RequestParameter();
        requestParameter.setResult(responseMessage);
        requestParameter.setRequest(responseMessage.getRequest());
        requestParameter.setUrl(responseMessage.getUrl());
        requestParameter.setRequestSocket(socket);
        requestParameter.setSession(SessionContainer.getSession(responseMessage.getSessionId()));
        requestParameter.setStatus(responseMessage.getCode());
        requestParameter.setRequestThread(this);
        requestParameter.setIp(socket.getInetAddress().getHostAddress());
        requestParameter.setHost(socket.getInetAddress().getHostName());
        requestParameter.setPort(socket.getPort());
        requestParameter.setFrom(responseMessage.getFrom());
        requestParameter.setTo(responseMessage.getTo());
        requestParameter.setRequestType(responseMessage.getRequestType());
        requestParameter.setProtocolVersion(responseMessage.getVersion());
        requestParameter.setServerContext(this.serverContext);
        requestParameter.setKeepAlive(responseMessage.isKeepAlive());
        ObjectMapper instance = ObjectMapperFactory.getInstance();
        try {
            Map map;
            if (responseMessage.getContent() == null || responseMessage.getContent().length() == 0) {
                map = new HashMap();
            }else {
                map = instance.readValue(responseMessage.getContent(), Map.class);
            }
             requestParameter.setRequestData(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return requestParameter;
    }

    /**
     * 对响应参数进行封装
     *
     * @param
     * @return
     */
    private ResponseParameter initResponseParameter() {
        ResponseParameter responseParameter = new ResponseParameter();
        responseParameter.setResponseSocket(socket);
        return responseParameter;
    }

    /**
     * 初始化配置文件中未被拦截的请求
     */
    private void initNotInterceptorBeans() {
        String configAttribute = ConfigUtils.getConfigAttribute(attribute);
        if (configAttribute == null) return;
        String[] split = configAttribute.split(",");
        for (String s : split) {
            list.add(Integer.parseInt(s));
        }
    }
}
