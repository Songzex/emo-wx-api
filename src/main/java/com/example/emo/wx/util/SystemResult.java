package com.example.emo.wx.util;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/*此类封装的是web端返还的数据*/
public class SystemResult  extends HashMap< String ,Object> {

    public SystemResult(){
        put("code", HttpStatus.SC_OK);
        put("msg","success");
    }
    public SystemResult put(String key,Object value){
        super.put(key,value);
        return this;
    }
    public static SystemResult ok(){
        return new SystemResult();
    }
    public static SystemResult ok(String msg){
        SystemResult r=new SystemResult();
        r.put("msg",msg);
        return r;
    }
    public static SystemResult ok(Map<String,Object> map){
        SystemResult r=new SystemResult();
        r.putAll(map);
        return r;
    }

    public static SystemResult error(int code,String msg){
        SystemResult r=new SystemResult();
        r.put("code",code);
        r.put("msg",msg);
        return r;
    }
    public static SystemResult error(String msg){
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR,msg);
    }
    public static SystemResult error(){
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR,"未知异常，请联系管理员");
    }
}
