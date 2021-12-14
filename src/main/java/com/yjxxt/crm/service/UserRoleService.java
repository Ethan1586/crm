package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.bean.UserRole;
import com.yjxxt.crm.mapper.UserRoleMapper;
import com.yjxxt.crm.query.RoleQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserRoleService extends BaseService<UserRole,Integer> {

    //注入
    @Autowired(required = false)
    private UserRoleMapper userRoleMapper;

}
