package com.example.emo.wx.config.shiro;

import org.springframework.stereotype.Component;
/**
 * 线程单享的一个对象
 * 三个api操作
 *
 * **/
@Component
public class ThreadLocalToken {
    private ThreadLocal<String> local=new ThreadLocal<>();

    public void setToken(String token){
        local.set(token);
    }

    public String getToken(){
        return local.get();
    }

    public void clear(){
        local.remove();
    }
}
