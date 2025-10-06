package zzuli.zw.config.common;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class SqlSessionTemplate {
    private final SqlSessionFactory sqlSessionFactory;

    public SqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public <T> T getMapper(Class<T> mapperClass) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            return sqlSession.getMapper(mapperClass);
        }
    }

    public <T> List<T> selectList(String statement, Object parameter) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            return session.selectList(statement, parameter);
        }
    }

    public int insert(String statement, Object parameter) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            return session.insert(statement, parameter);
        }
    }

    public int update(String statement, Object parameter) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            return session.update(statement, parameter);
        }
    }

    public int delete(String statement, Object parameter) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            return session.delete(statement, parameter);
        }
    }
}

