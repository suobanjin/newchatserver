package zzuli.zw.main.baseRequest;

import zzuli.zw.main.exception.ServerException;
import zzuli.zw.main.model.RequestParameter;
import zzuli.zw.main.model.ResponseParameter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName Request
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/2/12 12:19
 * @Version 1.0
 */
public abstract class BaseRequest {
    public abstract void doRequest(RequestParameter request, ResponseParameter response) throws IOException, InvocationTargetException, IllegalAccessException, ServerException, Exception;

}
