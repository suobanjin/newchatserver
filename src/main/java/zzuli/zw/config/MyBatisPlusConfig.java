package zzuli.zw.config;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import zzuli.zw.config.common.SqlSessionTemplate;
import zzuli.zw.main.annotation.Configuration;
import zzuli.zw.main.annotation.IOC;
import zzuli.zw.main.annotation.Value;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class MyBatisPlusConfig {

    @IOC
    public DataSource dataSource(@Value("${jdbc.url}") String url,
                                 @Value("${jdbc.username}") String username,
                                 @Value("${jdbc.password}") String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }

    //    @IOC
//    public SqlSessionFactory sqlSessionFactory() throws SQLException {
//        MybatisSqlSessionFactoryBuilder builder = new MybatisSqlSessionFactoryBuilder();
//        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
//        // 可选：配置 MapperLocations、TypeAliases、插件等
//        configuration.setMapUnderscoreToCamelCase(true);
//        configuration.addInterceptor(new PaginationInterceptor());
//        return builder.build(configuration);
//    }
//
//    @IOC
//    public SqlSession sqlSession(SqlSessionFactory sqlSessionFactory, DataSource dataSource) throws Exception {
//        return sqlSessionFactory.openSession(dataSource.getConnection());
//    }
    @IOC
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInterceptor());
        return interceptor;
    }

    @IOC
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, MybatisPlusInterceptor interceptor) throws Exception {
        // 使用 MyBatis 原生的方式构建 SqlSessionFactory
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.addInterceptor(interceptor); // 注册插件（分页、性能等）
        configuration.setMapUnderscoreToCamelCase(true);

        // 这里不依赖 Spring，直接创建 SqlSessionFactory
        Environment environment = new Environment("dev", new JdbcTransactionFactory(), dataSource);
        configuration.setEnvironment(environment);

        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        return builder.build(configuration);
    }

    @IOC
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        // 自定义封装 SqlSession（非 Spring 版本）
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
