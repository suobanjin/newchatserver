package zzuli.zw.main.ioc.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Proxy;

public class MybatisSessionTemplate {
    private final SqlSessionFactory sqlSessionFactory;

    public MybatisSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapperProxy(Class<T> mapperClass) {
        // 返回一个 JDK 代理：每次方法调用都会打开一个新的 SqlSession，执行 mapper 方法并关闭 session
        return (T) Proxy.newProxyInstance(mapperClass.getClassLoader(),
                new Class[]{mapperClass},
                (proxy, method, args) -> {
                    try (SqlSession session = sqlSessionFactory.openSession(true)) { // autoCommit = true
                        Object mapper = session.getMapper(mapperClass);
                        return method.invoke(mapper, args);
                    }
                });
    }

    // 也提供直接获取真实 mapper（需要自己管理 session）
    public <T> T getMapper(Class<T> mapperClass) {
        SqlSession session = sqlSessionFactory.openSession(true);
        return session.getMapper(mapperClass);
    }
}

