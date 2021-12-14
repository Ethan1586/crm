package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.mapper.UserMapper;
import com.yjxxt.crm.service.PermissionService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    //自动装配
    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    //系统登录页
    @RequestMapping("index")
    public String index(){
        return "index";
    }

    //欢迎页面
    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }

    //后台资源页面
    @RequestMapping("main")
    public String main(HttpServletRequest req){
        //获取当前用户的id
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //获取用户信息
        User user = userService.selectByPrimaryKey(userId);
        //存储
        req.setAttribute("user",user);
        //将用户的权限码存在session
        List<String> permissions = permissionService.queryUserHasRolesHasPermissions(userId);
        for (String code:permissions) {
            System.out.println(code+"权限码");
        }
        req.getSession().setAttribute("permissions",permissions);
        //转发
        return "main";
    }

}
