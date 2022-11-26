package zzuli.zw.main.interfaces;

import zzuli.zw.main.model.RequestParameter;

import zzuli.zw.main.model.ResponseParameter;
import zzuli.zw.main.model.protocol.ResponseMessage;

import java.lang.reflect.Parameter;
/**
 *@ClassName: HandlerMethodArgumentResolver
 *@Description: 参数解析器接口设计
 *@Author 索半斤
 *@Date 2022/3/13
 *@Version 1.0
 */
public interface HandlerMethodArgumentResolver {

    /***
     * @Author 索半斤
     * @Description 判断解析器是否支持parameter参数的解析
     * @Date 2022/3/13 21:29 
     * @Param [parameter]
     * @return boolean
     **/
    boolean supportsParameter(Parameter parameter);

    /***
     * @Author 索半斤
     * @Description 将方法参数从给定请求解析为参数值并返回
     * @Date 2022/3/13 21:33
     * @Param [parameter, request, response, responseMessage]
     * @return java.lang.Object
     **/
    Object resolveArgument(Parameter parameter,
                           RequestParameter request,
                           ResponseParameter response,
                           ResponseMessage responseMessage) throws Exception;
}
