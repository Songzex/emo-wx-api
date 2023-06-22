package com.example.emo.wx.config.shiro;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")// 又单例对象变成多例对象
/**
 * @Class AuthenticatingFilter   org.apache.shiro.web.filter.authc.AuthenticatingFilter;
 * **/
public class OAuth2Filter extends AuthenticatingFilter {
    /**
     * ThreadLocalToken  等于===== ThreadLocal<String> local=new ThreadLocal<>() 是一个线程的独有资源封装对象 每个线程单享的
     *  get set remove   实现对封装信息的一个存取移除
     * **/
    @Autowired
    private ThreadLocalToken threadLocalToken;
/**
 * 数据塞进redis 的过期时间
 * @Value  注入配置文件的属性
 * **/
/*    @Value("${emos.jwt.cache-expire}")//注解 值 的注入
    private int cacheExpire;*/
/**
 *使用 JWT 的工具类生成我们的token令牌
 *
 *  **/
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 实现 RedisTemplate和redis数据库里边数据操作的一个对象
     * **/
    @Autowired
    private RedisTemplate redisTemplate;
/**
 * 封装传入token变成token对象 然后交给shiro进行校验是否合格  先判定是否为空
 * **/
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req= (HttpServletRequest) request;
        String token=getRequestToken(req);
        if(StrUtil.isBlank(token)){
            return null;
        }
        return new OAuth2Token(token);
    }
/**
 *  拦截请求 对应小程序的请求的鉴别
 * **/
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req= (HttpServletRequest) request;
        if(req.getMethod().equals(RequestMethod.OPTIONS.name())){//options方法不执行
            return true;//为true shiro 不处理
        }//除了options请求之外 所有的请求都交给shiro处理
        return false;
    }
/**
 * 该方法用于处理所有的能被shiro处理请求的
 * 认证成功的时候 与后边的认证失败的情况 提示作用 增强可扩展性
 * **/
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req= (HttpServletRequest) request;
        HttpServletResponse resp= (HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));//允许跨域请求
        threadLocalToken.clear();//清空本地线程的数据对象（单向空间） 前提判定是要token 需要刷新 要做清理

        String token=getRequestToken(req);
        if(StrUtil.isBlank(token)){//判定token是否为空
            resp.setStatus(HttpStatus.SC_UNAUTHORIZED);//返还错误信息
            resp.getWriter().print("无效的令牌");//写入io里边
            return false;
        }
        try{
            jwtUtil.verifierToken(token);//校验token
        }catch (TokenExpiredException e){//TokenExpiredException 过期一场
            if(redisTemplate.hasKey(token)){//查询redis是否过期 是
                redisTemplate.delete(token);//删除token
                int userId=jwtUtil.getUserid(token);// 拿到uid
                token=jwtUtil.creteToken(userId);//生成新的token
                redisTemplate.opsForValue().set(token,userId+"",10, TimeUnit.DAYS);//存储redis中
                threadLocalToken.setToken(token);//封装到 ThreadLocal 中
            }
            else{
                resp.setStatus(HttpStatus.SC_UNAUTHORIZED);// 服务端没有令牌  请重新登录
                resp.getWriter().print("令牌已过期");
                return false;
            }
        }catch (Exception e){
            resp.setStatus(HttpStatus.SC_UNAUTHORIZED);// token格式不对
            resp.getWriter().print("无效的令牌");
            return false;
        }
        boolean bool=executeLogin(request,response);// 以上 token 过期  格式是否正确 是否有效
        // 的逻辑判定结束后 就可以交给shiro →由shiro框架的校验方法了进一步判定 这里直接调用executeLogin(request,response)方法 其实是间接调用relam类的方法
        return bool;
    }
// 认证失败的时候方法 便于做扩展
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletRequest req= (HttpServletRequest) request;
        HttpServletResponse resp= (HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
        try{
            resp.getWriter().print(e.getMessage());//
        }catch (Exception exception){

        }

        return false;
    }
/**
 * doFilter 最先执行的 过滤连的开始者 执行链  super.doFilterInternal(request, response, chain); 掌管拦截和响应的对象
 * **/
    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req= (HttpServletRequest) request;
        HttpServletResponse resp= (HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        super.doFilterInternal(request, response, chain);

    }
/**
 * 获取从请求头里边的token
 * 然后返还
 * **/
    private String getRequestToken(HttpServletRequest request){
        String token=request.getHeader("token");
        if(StrUtil.isBlank(token)){
            token=request.getParameter("token");
        }
        return token;
    }
}
