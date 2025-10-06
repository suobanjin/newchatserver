package zzuli.zw.config;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import zzuli.zw.config.pojo.PageParam;

import java.sql.Connection;
import java.util.Properties;

/**
 * 简易分页拦截器（MyBatis 3.x 兼容）
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class PaginationInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        String originalSql = statementHandler.getBoundSql().getSql();

        // 如果存在分页参数（例如 limitStart/limitSize）
        Object param = statementHandler.getBoundSql().getParameterObject();
        if (param instanceof PageParam) {
            PageParam page = (PageParam) param;

            String newSql = originalSql + " LIMIT " + page.getOffset() + ", " + page.getLimit();
            // 通过反射修改 SQL
            MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
            metaObject.setValue("delegate.boundSql.sql", newSql);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
