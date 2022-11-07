package zzuli.zw.mapper;

import zzuli.zw.main.annotation.Bean;
import zzuli.zw.utils.TxQueryRunner;

import java.sql.SQLException;

/**
 * @author 索半斤
 * @description 信息相关操作
 * @date 2022/1/19
 * @className MessageMapperImpl
 */
@Bean("messageMapper")
public class MessageMapperImpl implements MessageMapper{
    TxQueryRunner queryRunner = new TxQueryRunner();
    @Override
    public int deleteMessagesByUserId(int userId) {
        String sql = "DELETE FROM message WHERE `to` = ? OR `from` = ?";
        try {
            return queryRunner.update(sql,userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
