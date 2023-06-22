package com.example.emo.wx.controller;

import com.example.emo.wx.controller.form.TestSayHelloForm;
import com.example.emo.wx.util.SystemResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/test")
@Api("接口测试swagger页面")
public class TestController {
    @PostMapping("/hello")
    @ApiOperation("测试方法")//解释说明
    public SystemResult sayHello(@Valid @RequestBody TestSayHelloForm form){
        return SystemResult.ok().put("massage","helloWord");
    }/**
     @Valid  验证数据要用这个注解  @RequestBody TestSayHelloForm form   @RequestBody 把JSON转换成对象
     **/
}
