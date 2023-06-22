package com.example.emo.wx.aop;

import com.example.emo.wx.config.shiro.ThreadLocalToken;
import com.example.emo.wx.util.SystemResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/****/
@Aspect//切面类
@Component
public class TokenAspect {
    @Autowired
    private ThreadLocalToken threadLocalToken;
/**切入点范围**/
    @Pointcut("execution(public * com.example.emo.wx.controller.*.*(..))")
    public void aspect(){

    }
/**环绕通知 方法前执行 方法后执行   实时监控令牌的产生并 刷新返还要给客户
 * ProceedingJoinPoint point  切入方法的返回值
 * 为什么要选择环绕 应为多个
 * **/
    @Around("aspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable{
        SystemResult r=(SystemResult)point.proceed();//这里有一个数据类型的改变 所以要强转一下
        String token=threadLocalToken.getToken();//检测是否有新的token
        if(token!=null){//判定为空
            r.put("token",token);//改变响应的返回值 添加token值 给前端
            threadLocalToken.clear();//在清楚线程的存储对象的值  就是一个额媒介
        }
        return r;//返还改变的数据对象 由springboot框架在对应的方法里调用 然后数据结果改变并返还给前端
    }
}
