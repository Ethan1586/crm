package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    public Integer countPermissionByRoleId(Integer roleId);

    public List<Integer> queryRoleHasAllModuleIdsByRoleId(Integer roleId);

    public List<String> queryUserHasRolesHasPermissions(Integer userId);

    public Integer deleteRoleModuleByRoleId(Integer roleId);


    int countPermissionsByModuleId(Integer mid);


    int deletePermissionsByModuleId(Integer mid);


}