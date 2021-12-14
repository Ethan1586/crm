package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.exceptions.ParamsException;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    //自动装配
    @Autowired
    private UserService userService;

    //用户登录
    @RequestMapping("login")
    @ResponseBody
    public ResultInfo userLogin(String userName,String userPwd){
        //实例化返回值对象
        ResultInfo resultInfo = new ResultInfo();
        //捕捉异常
        //try {
            //调用service层的方法 返回用户对象
            UserModel userModel = userService.userLogin(userName, userPwd);
            //将返回的用户对象设置到resultInfo对象里面
            resultInfo.setResult(userModel);
//        }catch (ParamsException pe){
//            //打印异常信息
//            pe.printStackTrace();
//            //设置状态码和提示信息
//            resultInfo.setCode(pe.getCode());
//            resultInfo.setMsg(pe.getMsg());
//        }catch (Exception e){
//            //打印异常信息
//            e.printStackTrace();
//            //设置状态码和提示信息
//            resultInfo.setCode(500);
//            resultInfo.setMsg("操作失败");
//        }
        //返回结果信息
        return resultInfo;
    }

    //用户界面
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    //用户添加修改界面
    @RequestMapping("addOrUpdatePage")
    public String addOrUpdatePage(Integer id, Model model){
        if (null!=id){
            //获取用户
            User user = userService.selectByPrimaryKey(id);
            //存储信息
            model.addAttribute("user",user);
        }
        return "user/add_update";
    }

    //修改密码
    @RequestMapping("updatePwd")
    @ResponseBody
    public ResultInfo updatePwd(HttpServletRequest req,String oldPassword,String newPassword,String confirmPwd){
        System.out.println(oldPassword);
        //实例化结果信息
        ResultInfo resultInfo = new ResultInfo();
        //获得cookie中的id信息
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //捕捉异常
        //try {
            //修改密码
            userService.changeUserPwd(userId,oldPassword,newPassword,confirmPwd);
//        }catch (ParamsException pe){
//            //打印异常信息
//            pe.printStackTrace();
//            //设置状态码和提示信息
//            resultInfo.setMsg(pe.getMsg());
//            resultInfo.setCode(pe.getCode());
//        }catch (Exception e){
//            //打印异常信息
//            e.printStackTrace();
//            //设置状态码和提示信息
//            resultInfo.setCode(500);
//            resultInfo.setMsg("操作失败");
//        }
        //返回结果信息
        return resultInfo;
    }

    //修改密码 视图转发
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }

    //修改用户基本信息
    @PostMapping("setting")
    @ResponseBody
    public ResultInfo updateUser(User user){
        //实例化结果信息
        ResultInfo resultInfo = new ResultInfo();
        //修改信息
        userService.updateByPrimaryKeySelective(user);
        //返回结果信息
        return resultInfo;
    }

    //用户基本资料 视图转发
    @RequestMapping("toSettingPage")
    public String toSettingPage(HttpServletRequest request){
        //获取用户id
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //获取用户信息
        User user = userService.selectByPrimaryKey(userId);
        //存储用户信息
        request.setAttribute("user",user);
        //转发
        return "user/setting";
    }

    //查询所有的销售人员
    @RequestMapping("sales")
    @ResponseBody
    public List<Map<String,Object>> findSales(){
        return userService.querySales();
    }

    //条件查询用户
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> findSUsers(UserQuery userQuery){
        return userService.findUserByParams(userQuery);
    }

    //添加用户基本信息
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveUser(User user){
        //修改信息
        userService.addUser(user);
        //返回结果信息
        return success("用户添加成功");
    }

    //修改用户基本信息
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo alterUser(User user){
        //修改信息
        userService.changeUser(user);
        //返回结果信息
        return success("用户修改成功");
    }

    //删除用户基本信息
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        //删除信息
        userService.deletesUser(ids);
        //返回结果信息
        return success("用户删除成功");
    }

}
