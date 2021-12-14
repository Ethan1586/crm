package com.yjxxt.crm;

import com.alibaba.fastjson.JSON;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.exceptions.NoLoginException;
import com.yjxxt.crm.exceptions.ParamsException;
import com.yjxxt.crm.interceptors.NoLoginInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        //未登陆异常
        if (ex instanceof NoLoginException){
            //捕捉到异常重定向
            ModelAndView mav = new ModelAndView("redirect:/index");
            return mav;
        }
        //设置默认异常处理
        //实例化对象
        ModelAndView mav = new ModelAndView("error");
        //存储信息
        mav.addObject("code","300");
        mav.addObject("msg","系统异常...");
        //判断HandlerMethod
        if (handler instanceof HandlerMethod){
            //强转
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取方法上的responseBody的注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            //判断注解是否存在
            if (null == responseBody){
                //方法返回视图
                if (ex instanceof ParamsException){
                    //打印异常信息
                    ex.printStackTrace();
                    //设置状态码和提示信息
                    mav.addObject("code",((ParamsException) ex).getCode());
                    mav.addObject("msg",((ParamsException) ex).getMsg());
                }
                return mav;
            }else {
                //方法返回json
                //实例化ResultInfo
                ResultInfo resultInfo = new ResultInfo();
                //存储异常信息
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常");
                //如果是捕获自定义异常
                if (ex instanceof ParamsException){
                    //强转
                    ParamsException pe = (ParamsException)ex;
                    //存储异常信息
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                //设置响应类型和编码格式
                response.setContentType("application/json;charset=utf-8");
                //得到输出流
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    out.write(JSON.toJSONString(resultInfo));
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (null != out){
                        out.close();
                    }
                }
                return null;
            }
        }
        return mav;
    }
}

