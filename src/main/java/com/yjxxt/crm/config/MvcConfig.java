package com.yjxxt.crm.config;

import com.yjxxt.crm.interceptors.NoLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Bean
    public NoLoginInterceptor noLoginInterceptor(){
        return new NoLoginInterceptor();
    }
    //重写方法
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截路径
        //配置拦截器
        registry.addInterceptor(noLoginInterceptor())
                //添加拦截路径
                .addPathPatterns("/**")
                //放行路径
                .excludePathPatterns("/index","/user/login","/js/**","/css/**","/images/**","/lib/**");
    }
}
