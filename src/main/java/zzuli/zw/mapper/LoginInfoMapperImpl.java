package zzuli.zw.mapper;
import zzuli.zw.main.annotation.Bean;
import zzuli.zw.utils.TxQueryRunner;
import java.sql.SQLException;
/**
 * @author 索半斤
 * @description 登录信息相关操作
 * @date 2022/1/19
 * @className LoginInfoMapperImpl
 */
@Bean("loginMapper")
public class LoginInfoMapperImpl implements LoginInfoMapper{
    private TxQueryRunner queryRunner = new TxQueryRunner();
    /**
     * @Author 索半斤
     * @Description 根据用户id删除用户登录信息
     * @Date 2022/1/19 20:29
     * @Param [userId]
     * @return int
     **/
    @Override
    public int deleteLoginInfoByUserId(int userId) {
        String sql = "DELETE FROM logininfo WHERE `user` = ?";
        try {
            return queryRunner.update(sql,userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
