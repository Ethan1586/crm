package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.Role;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {
    //查询所有的角色
    @MapKey("")
    public List<Map<String,Object>> queryRoles(Integer userId);

    //根据姓名查询角色
    public Role selectRoleByName(String name);
}