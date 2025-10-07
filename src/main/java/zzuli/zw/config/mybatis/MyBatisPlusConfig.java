package zzuli.zw.config.mybatis;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import zzuli.zw.config.common.SqlSessionTemplate;
import zzuli.zw.main.annotation.Configuration;
import zzuli.zw.main.annotation.IOC;
import zzuli.zw.main.annotation.Value;
import zzuli.zw.main.ioc.mybatis.MybatisSessionTemplate;
import javax.sql.DataSource;

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

    @IOC
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInterceptor());
        return interceptor;
    }

    @IOC
    public MybatisConfiguration mybatisConfiguration(DataSource dataSource, MybatisPlusInterceptor interceptor) {
        // 创建 MyBatis-Plus Configuration
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);

        //添加分页插件
        configuration.addInterceptor(interceptor);

        Environment environment = new Environment("dev", new JdbcTransactionFactory(), dataSource);
        configuration.setEnvironment(environment);
        return configuration;
    }

    @IOC
    public SqlSessionFactory sqlSessionFactory(MybatisConfiguration configuration) throws Exception {
        return new MybatisSqlSessionFactoryBuilder().build(configuration);
    }

    @IOC
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        // 自定义封装 SqlSession（非 Spring 版本）
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @IOC
    public MybatisSessionTemplate mybatisSessionTemplate(SqlSessionFactory sqlSessionFactory){
        return new MybatisSessionTemplate(sqlSessionFactory);
    }
}
