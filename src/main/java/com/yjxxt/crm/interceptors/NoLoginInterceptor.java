package com.yjxxt.crm.interceptors;


import com.yjxxt.crm.exceptions.NoLoginException;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoLoginInterceptor extends HandlerInterceptorAdapter {
    //引用service接口
    @Autowired
    private UserService userService;
    //重写pre方法

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取Cookie中的用户id
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //未登录-->拦截
        if (userId==null || userService.selectByPrimaryKey(userId)==null){
            //抛出异常
            throw new NoLoginException("用户未登录...");
        }
        //放行
        return true;
    }
}
