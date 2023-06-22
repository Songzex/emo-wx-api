package com.example.emo.wx.config.xss;

import com.example.emo.wx.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * 精简返还springmvc的异常信息 不显示异常的详细信息
 *
 * **/
@Slf4j
@RestControllerAdvice// rest风格的 能捕捉springmvc的全部异常信息
public class ExceptionAdvice {
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//
    @ExceptionHandler(Exception.class)//全局捕获异常
    public String exceptionHandler(Exception e){
        log.error("执行异常",e);
        if(e instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException exception= (MethodArgumentNotValidException) e;
            return exception.getBindingResult().getFieldError().getDefaultMessage();
        }
        else if(e instanceof EmosException){
            EmosException exception= (EmosException) e;
            return exception.getMsg();
        }
        else if(e instanceof UnauthorizedException){//shiro的异常
            return "你不具备相关权限";
        }
        else{
            return "后端执行异常";//普通的异常
        }
    }
}
