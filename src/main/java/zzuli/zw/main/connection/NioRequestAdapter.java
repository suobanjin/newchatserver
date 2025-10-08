package zzuli.zw.main.connection;

import zzuli.zw.main.factory.ObjectMapperFactory;
import zzuli.zw.main.factory.SessionContainer;
import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.main.model.InterceptorChain;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.model.protocol.ResponseMessage;

import java.util.HashMap;
import java.util.Map;

public class NioRequestAdapter {

    private final ServerContext serverContext;

    public NioRequestAdapter(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public void process(NioConnection connection, ResponseMessage responseMessage) {
        RequestParameter request = convertToRequestParameter(connection, responseMessage);
        ResponseParameter response = new ResponseParameter();
        response.setNioConnection(connection);

        InterceptorChain chain = serverContext.getInterceptorChain();
        DispatcherRequest dispatcherRequest = new DispatcherRequest();

        try {
            if (chain.applyPreHandle(request, response, null)) {
                dispatcherRequest.doRequest(request, response);
                chain.applyPostHandle(request, response, null, null);
            }
            chain.triggerAfterCompletion(request, response, null, null);
        } catch (Exception e) {
            try {
                chain.triggerAfterCompletion(request, response, null, e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }

    private RequestParameter convertToRequestParameter(NioConnection connection, ResponseMessage msg) {
        RequestParameter request = new RequestParameter();
        request.setResult(msg);
        request.setRequest(msg.getRequest());
        request.setUrl(msg.getUrl());
        request.setIp(connection.getRemoteAddress().getHostString());
        request.setSession(SessionContainer.getSession(msg.getSessionId()));
        request.setServerContext(serverContext);
        request.setRequestData(parseContent(msg.getContent()));
        return request;
    }

    private Map<Object, Object> parseContent(String content) {
        if (content == null || content.isEmpty()) return new HashMap<>();
        try {
            return ObjectMapperFactory.getInstance().readValue(content, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}

