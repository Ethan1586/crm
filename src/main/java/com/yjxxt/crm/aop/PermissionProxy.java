package com.yjxxt.crm.aop;

import com.yjxxt.crm.annotation.RequirePermission;
import com.yjxxt.crm.exceptions.NoAuthException;
import com.yjxxt.crm.exceptions.NoLoginException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@Aspect
public class PermissionProxy {
    //注入
    @Autowired
    private HttpSession session;
    @Around(value = "@annotation(com.yjxxt.crm.annotation.RequirePermission)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        //获取用户的权限码
        List<String> permissions = (List<String>) session.getAttribute("permissions");
        if (null==permissions || permissions.size()==0){
            throw new NoLoginException("未登陆异常");
        }
        //判断是否有访问目标资源的权限码
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        RequirePermission requirePermission = methodSignature.getMethod().getDeclaredAnnotation(RequirePermission.class);
        //比对
        if (!(permissions.contains(requirePermission.code()))){
            throw new NoAuthException("无权限访问");
        }
        Object result = pjp.proceed();

        return result;
    }
}
