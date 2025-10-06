package zzuli.zw.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import zzuli.zw.main.factory.ObjectMapperFactory;
import zzuli.zw.pojo.User;
import zzuli.zw.pojo.model.ResponseModel;

/**
 * @ClassName JacksonTest
 * @Description: TODO
 * @Author 索半斤
 * @Datetime 2022年 11月 27日 15:42
 * @Version: 1.0
 */
public class JacksonTest {
    @Test
    public void test01() throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapperFactory.getInstance();
        ResponseModel<User> responseModel = new ResponseModel<>();
        User user = new User();
        user.setAccount("1234");
        responseModel.setData(user);
        responseModel.setContent("123");
        String s = objectMapper.writeValueAsString(responseModel);

        ResponseModel<User> userResponseModel = objectMapper.readValue(s, new TypeReference<ResponseModel<User>>() {
        });
        System.out.println(userResponseModel);
    }

}
