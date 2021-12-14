package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Permission;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.mapper.RoleMapper;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RoleService extends BaseService<Role,Integer> {
    //注入
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private ModuleMapper moduleMapper;

    //查找所有角色
    public List<Map<String,Object>> selectRoles(Integer userId){
        return roleMapper.queryRoles(userId);
    }

    //分页查询
    public Map<String,Object> findRolesByParams(RoleQuery roleQuery){
        //实例化map
        Map<String,Object> map = new HashMap<String,Object>();
        //开启分页
        PageHelper.startPage(roleQuery.getPage(),roleQuery.getLimit());
        PageInfo<Role> rList = new PageInfo<>(selectByParams(roleQuery));
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",rList.getTotal());
        map.put("data",rList.getList());
        //返回结果信息
        return map;
    }
    //添加角色信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRole(Role role){
        //角色名不能为空
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名不能为空");
        //角色名唯一
        Role temp = roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(null!=temp,"该角色已存在");
        //设置默认值
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        //检验是否成功
        AssertUtil.isTrue(roleMapper.insertHasKey(role)<1,"增加角色信息失败");
    }
    //修改角色信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeRole(Role role){
        //验证对象是否存在
        Role temp = roleMapper.selectByPrimaryKey(role.getId());
        AssertUtil.isTrue(temp==null,"请选择待修改的角色信息");
        //角色名唯一
        Role temp2 = roleMapper.selectRoleByName(role.getRoleName());
        AssertUtil.isTrue(null!=temp2 && !(role.getId().equals(temp2.getId())),"角色已经存在");
        //设定默认值
        role.setUpdateDate(new Date());
        //检验是否成功
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"角色信息修改失败");
    }

    //删除角色信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeRole(Role role){
        //验证对象是否存在
        Role temp = roleMapper.selectByPrimaryKey(role.getId());
        AssertUtil.isTrue(temp==null || selectByPrimaryKey(role.getId())==null,"请选择待删除的角色信息");
        //设定默认值
        role.setIsValid(0);
        role.setUpdateDate(new Date());
        //检验是否成功
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"角色信息删除失败");
    }

    //添加授权
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer[] mids,Integer roleId){
        //根据id获取角色信息  验证
        Role temp = selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null==temp || null==roleId,"请选择要操作的角色");
        //如果角色存在用户权限删除角色原始权限  添加新权限
        int count = permissionMapper.countPermissionByRoleId(roleId);
        if (count>0){
            AssertUtil.isTrue(permissionMapper.deleteRoleModuleByRoleId(roleId)!=count,"权限分配失败");
        }
        //设置默认值
        if (null!=mids || mids.length>0){
            List<Permission> permissions = new ArrayList<Permission>();
            //遍历
            for (Integer mid:mids) {
                //实例化permission对象
                Permission permission = new Permission();
                permission.setModuleId(mid);
                permission.setRoleId(roleId);
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                permissions.add(permission);
            }
            permissionMapper.insertBatch(permissions);
        }
    }
}
