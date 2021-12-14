package com.yjxxt.crm.controller;

import com.yjxxt.crm.annotation.RequirePermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {
    //注入
    @Resource
    private RoleService roleService;

    //查找所有角色
    @RequestMapping("findRoles")
    @ResponseBody
    public List<Map<String, Object>> findRoles(Integer userId) {
        return roleService.selectRoles(userId);
    }

    //分页查询
    @RequestMapping("list")
    @ResponseBody
   // @RequirePermission(code = "60")
    public Map<String, Object> findRolesByParams(RoleQuery roleQuery) {

        return roleService.findRolesByParams(roleQuery);
    }

    //角色管理页面
    @RequestMapping("index")
    public String index() {
        return "role/role";
    }

    //添加角色
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(Role role) {
        roleService.addRole(role);
        return success("角色信息添加成功");
    }

    //修改角色
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(Role role) {
        roleService.changeRole(role);
        return success("角色信息修改成功");
    }

    //添加/修改页面
    @RequestMapping("addOrUpdateRolePage")
    public String addOrUpdateRolePage(Integer userId, Model model) {
        //校验
        if (userId != null) {
            //获取role
            Role role = roleService.selectByPrimaryKey(userId);
            //存储role信息
            model.addAttribute("role", role);
        }
        return "role/add_update";
    }

    //删除角色
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo del(Role role) {
        roleService.removeRole(role);
        return success("角色信息删除成功");
    }

    //授权页面
    @RequestMapping("toRoleGrantPage")
    public String toRoleGrantPage(Integer roleId, Model model) {
        //存储roleId信息
        model.addAttribute("roleId", roleId);
        return "role/grant";
    }

    //授权里页面
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer[] mids,Integer roleId){
        roleService.addGrant(mids, roleId);
        return success("角色信息授权成功");
    }
}
