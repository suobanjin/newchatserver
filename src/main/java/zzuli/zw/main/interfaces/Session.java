package zzuli.zw.main.interfaces;

import java.util.List;

/**
 *@ClassName: Session
 *@Description: 设计Session接口规范
 *@Author 索半斤
 *@Date 2022/3/13
 *@Version 1.0
 */
public interface Session {
    /***
     * @Author 索半斤
     * @Description 设置Session属性
     * @Date 2022/3/13 21:35
     * @Param [id, o] session的唯一ID，属性
     * @return boolean
     **/
    boolean setAttribute(String id,Object o);
    /***
     * @Author 索半斤
     * @Description 获取session属性
     * @Date 2022/3/13 21:36
     * @Param [id]
     * @return java.lang.Object
     **/
    Object getAttribute(String id);

    String getId();
    void removeAttribute(String id);
    long getCreatorTime();

    List<Object> getAttributes();
    List<String> getAttributeNames();

}
