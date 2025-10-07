package zzuli.zw.config.mybatis;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MybatisPlusInterceptor implements Interceptor {

    private final List<Interceptor> innerInterceptors = new ArrayList<>();

    public void addInnerInterceptor(Interceptor interceptor) {
        innerInterceptors.add(interceptor);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        for (Interceptor interceptor : innerInterceptors) {
            result = interceptor.intercept(invocation);
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        Object wrapped = target;
        for (Interceptor interceptor : innerInterceptors) {
            wrapped = interceptor.plugin(wrapped);
        }
        return wrapped;
    }

    @Override
    public void setProperties(Properties properties) {
        for (Interceptor interceptor : innerInterceptors) {
            interceptor.setProperties(properties);
        }
    }
}

