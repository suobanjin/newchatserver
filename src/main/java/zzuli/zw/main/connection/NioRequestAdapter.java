package zzuli.zw.main.connection;

import lombok.extern.slf4j.Slf4j;
import zzuli.zw.main.ioc.ServerContext;
import zzuli.zw.main.model.InterceptorChain;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;

import java.io.IOException;

@Slf4j
public class NioRequestAdapter {

    private final ServerContext serverContext;
    private final NioRequestDispatcher dispatcherRequest;
    public NioRequestAdapter(ServerContext serverContext) {
        this.serverContext = serverContext;
        this.dispatcherRequest = new NioRequestDispatcher(this.serverContext);
    }

    public void process(RequestParameter request, ResponseParameter response) {

        InterceptorChain chain = serverContext.getInterceptorChain();

        try {
            if (chain.applyPreHandle(request, response, null)) {
                dispatcherRequest.dispatchRequest(request, response);
                chain.applyPostHandle(request, response, null, null);
            }
            chain.triggerAfterCompletion(request, response, null, null);
        } catch (Exception e) {
            try {
                chain.triggerAfterCompletion(request, response, null, e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            log.error("处理请求异常", e);
        }
    }
}

