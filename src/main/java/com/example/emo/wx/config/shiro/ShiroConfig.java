package com.example.emo.wx.config.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
/**创建shiro 关键组件对象**/
@Configuration
public class ShiroConfig {
      /**创建securityManager组件对象 管控realm   **/
    @Bean("securityManager")
    public SecurityManager securityManager(OAuth2Realm realm){
        DefaultWebSecurityManager securityManager=new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    /**把自己的Filter 封装到shiro的中shiroFilter**/
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager,OAuth2Filter filter){
        ShiroFilterFactoryBean shiroFilter=new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
//创建map集合存我们的filter 然后封装到 shiroFilter中去执行我们的请求的过滤规则
        Map<String , Filter> map=new HashMap<>();
        map.put("oauth2",filter);
        shiroFilter.setFilters(map);
//过滤的路径
        Map<String,String> filterMap=new LinkedHashMap<>();
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/druid/**", "anon");
        filterMap.put("/app/**", "anon");
        filterMap.put("/sys/login", "anon");
        filterMap.put("/swagger/**", "anon");
        filterMap.put("/v2/api-docs", "anon");
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/swagger-resources/**", "anon");
        filterMap.put("/captcha.jpg", "anon");
        filterMap.put("/user/register", "anon");
        filterMap.put("/user/login", "anon");
        filterMap.put("/test/**", "anon");
        filterMap.put("/meeting/recieveNotify", "anon");
        filterMap.put("/**", "oauth2");
//封装到 shiroFilter的FilterChainDefinitionMap中  然后执行 anon 不用拦截的
        shiroFilter.setFilterChainDefinitionMap(filterMap);

        return shiroFilter;

    }
  /**
   * 管理shiro对象的生命周期
   * **/
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }
/**
 *
 * AOP切面类  AuthorizationAttributeSourceAdvisor 时 里边有具体的擦操作
 * 作用在在执行web方法时时要进行的一个额外操作 比如验证权限 就是在一个程序执行的过程中想进行一下其他操作干扰改变运行的结果
 *就是一个aop的应用   所示为了给aop技术支持的一个对象
 *
 *
 * AuthorizationAttributeSourceAdvisor的作用就是在AOP操作中，基于安全注解和配置，将安全规则应用于方法或类级别的切点。
 *
 * 具体来说，AuthorizationAttributeSourceAdvisor会检测方法或类上的安全注解（如@Secured、@PreAuthorize、@PostAuthorize等），
 * 或者通过配置（如<intercept-url>元素）定义的安全规则，并将这些安全规则转化为Spring Security的Advice（通知）。
 * Advice会在方法执行前或执行后执行相关的安全检查，以确保当前用户是否具有执行该方法或访问该资源的权限。
 * **/
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor advisor=new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
