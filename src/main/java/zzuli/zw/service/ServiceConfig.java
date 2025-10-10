package zzuli.zw.service;

import zzuli.zw.main.annotation.Configuration;
import zzuli.zw.main.annotation.IOC;
import zzuli.zw.request.FriendRequest;

@Configuration
public class ServiceConfig {

    @IOC
    public String test(FriendRequest friendRequest){
        friendRequest.test();
        System.out.println("12345");
        return "1";
    }
}
