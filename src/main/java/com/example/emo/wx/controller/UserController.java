package com.example.emo.wx.controller;

import cn.hutool.json.JSONUtil;
import com.example.emo.wx.util.SystemResult;
import com.example.emo.wx.config.shiro.JwtUtil;
import com.example.emo.wx.controller.form.*;
import com.example.emo.wx.exception.EmosException;
import com.example.emo.wx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Api("用户模块Web接口")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${emo.jwt.cache-expire}")
    private int cacheExpire;

    @PostMapping("/register")
    @ApiOperation("注册用户")
    public SystemResult register(@Valid @RequestBody RegisterForm form){
        System.out.println("helloworld");
        int id=userService.registerUser(form.getRegisterCode(),form.getCode(),form.getNickname(),form.getPhoto());
        String token=jwtUtil.creteToken(id);
        Set<String> permsSet=userService.searchUserPermissions(id);
        saveCacheToken(token,id);
        System.out.println(
                123
        );
        return SystemResult.ok("用户注册成功").put("token",token).put("permission",permsSet);
    }

  private void saveCacheToken(String token,int userId){
        redisTemplate.opsForValue().set(token,userId+"",cacheExpire, TimeUnit.DAYS);//将token保存在redis中
    }
}
