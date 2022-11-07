package zzuli.zw.service;

import zzuli.zw.domain.User;

/**
 * @ClassName LoginService
 * @Description TODO
 * @Author 索半斤
 * @Date 2021/1/14 22:24
 * @Version 1.0
 */
public interface LoginService {
    String findHeadPictureByUsername(String username);
    boolean login(User user);
}
