package com.example.emo.wx.config.shiro;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
/**此类封装了jwt的校验方法**/
@Component
@Slf4j
public class JwtUtil {
    @Value("${emo.jwt.secret}")  /*把注解的属性值注入到这个变量里边*/
    private  String secret;
    @Value("${emo.jwt.expire}")
    private  int expire;
    /**生成token的方法**/
    public  String creteToken(int userId) {
        DateTime  date = DateUtil.offset(new Date(), DateField.DAY_OF_YEAR, 5);
        Algorithm algorithm = Algorithm.HMAC256(secret);//静态工厂创建对象 加密方法
        JWTCreator.Builder builder = JWT.create();
        String token = builder.withClaim("userId", userId).withExpiresAt(date).sign(algorithm);
        return token;
    }
    /**从token中获取userid**/
    public int getUserid(String token) {
        DecodedJWT jwt = JWT.decode(token);// jwt的api
        Integer userId = jwt.getClaim("userId").asInt();
        return  userId;
    }
    /**校验token是否正确的**/
    public void  verifierToken (String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);//得到算法对象
        JWTVerifier verifier = JWT.require(algorithm).build();//创建验证对象
        verifier.verify(token);//具体的验证方法


    }


}
