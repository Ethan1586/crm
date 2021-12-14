package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.bean.UserRole;
import com.yjxxt.crm.mapper.UserMapper;
import com.yjxxt.crm.mapper.UserRoleMapper;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {
    //自动装配
    @Resource
    private UserMapper userMapper;

    //自动装配
    @Resource
    private UserRoleMapper userRoleMapper;

    //用户登录校验
    public UserModel userLogin(String userName,String userPwd){
        //验证参数
        checkLoginParams(userName,userPwd);
        //验证用户名是否存在
        User user = userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(user==null,"用户名不存在");
        //用户名密码是否正确
        checkLoginPwd(userPwd,user.getUserPwd());
        //返回用户的相关信息
        return buildUserInfo(user);
    }

    /**
     * 构建返回用户的信息
     * @param user
     * @return
     */
    private UserModel buildUserInfo(User user){
        //实例化userModel对象
        UserModel userModel = new UserModel();
        //设置用户信息
        //用户id加密
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    //修改用户密码
    public void changeUserPwd(Integer userId,String oldPassword,String newPassword,String confirmPwd){
        //获取用户信息
        User user = userMapper.selectByPrimaryKey(userId);
        //密码验证
        checkPasswordParams(user,oldPassword,newPassword,confirmPwd);
        //修改密码
        user.setUserPwd(Md5Util.encode(newPassword));
        //检验是否修改成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改失败");
    }

    //密码验证
    private void checkPasswordParams(User user,String oldPassword,String newPassword,String confirmPwd) {
        //用户未登录或不存在
        AssertUtil.isTrue(user==null,"用户未登录或不存在");
        //原始密码不能为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"原始密码不能为空");
        //原始密码是否正确
        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))),"原始密码不正确");
        //新密码不能为空
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"新密码不能为空");
        //新密码不能与原始密码相同
        AssertUtil.isTrue(oldPassword.equals(newPassword),"新密码不能与原始密码一致");
        //确认密码不能为空
        AssertUtil.isTrue(StringUtils.isBlank(confirmPwd),"确认密码不能为空");
        //确认密码与新密码相同
        AssertUtil.isTrue(!newPassword.equals(confirmPwd),"确认密码应与新密码保持一致");
    }



    /**
     * 验证密码
     * @param userPwd
     * @param userPwd1
     */
    private void checkLoginPwd(String userPwd, String userPwd1) {
        //获取加密密码
        userPwd = Md5Util.encode(userPwd);
        //验证密码
        AssertUtil.isTrue(!userPwd.equals(userPwd1),"用户密码不正确");
    }

    /**
     * 验证用户参数
     * @param userName
     * @param userPwd
     */
    private void checkLoginParams(String userName, String userPwd) {
        //用户名不能为空
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空!");
        //用户密码不能为空
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空");
    }


    //获取销售人员信息
    public List<Map<String,Object>> querySales(){
        return userMapper.selectSales();
    }

    //分页查询用户模块信息
    public Map<String,Object> findUserByParams(UserQuery userQuery){
        //实例化map
        Map<String,Object> map = new HashMap<String,Object>();
        //初始化分页
        PageHelper.startPage(userQuery.getPage(),userQuery.getLimit());
        //开始分页
        PageInfo<User> pList = new PageInfo<User>(selectByParams(userQuery));
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pList.getTotal());
        map.put("data",pList.getList());
        //返回map
        return map;
    }

    //验证添加用户信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user){
        //检查用户参数
        checkUser(user.getUserName(),user.getPhone(),user.getEmail());
        //用户名唯一
        User temp = userMapper.queryUserByUserName(user.getUserName());
        AssertUtil.isTrue(temp!=null,"用户名已存在");
        //设定状态默认值 时间
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //密码加密
        user.setUserPwd(Md5Util.encode("123456"));
        //校验是否修改成功
        AssertUtil.isTrue(insertSelective(user)<1,"添加失败");
        //System.out.println(user.getId()+"---"+user.getRoleIds());
        //批量操作中间表
        relationUserRole(user.getId(),user.getRoleIds());
    }

    //批量操作中间表
    private void relationUserRole(Integer userId, String roleIds) {
        //准备集合收集数据
        List<UserRole> uRList = new ArrayList<UserRole>();
        //检验角色信息是否存在
        AssertUtil.isTrue(StringUtils.isBlank(roleIds),"请选择角色信息");
        //统计用户有多少角色
        int count = userRoleMapper.countUserRoleNum(userId);
        //删除已有角色
        if (count>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"原角色信息删除失败");
        }
        //增加角色
        String[] roleStrId = roleIds.split(",");
        //遍历
        for (String rid:roleStrId
             ) {
            //准备对象
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(Integer.parseInt(rid));
            userRole.setCreateDate(new Date());
            userRole.setUpdateDate(new Date());
            //存放到集合
            uRList.add(userRole);
        }
        //批量增加|修改
        AssertUtil.isTrue(userRoleMapper.insertBatch(uRList)!=uRList.size(),"用户角色分配失败");
    }

    //检查用户参数
    private void checkUser(String userName,String phone,String email) {
        //用户名不能为空
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        //邮箱不能为空
        AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空");
        //电话号码不能为空
        AssertUtil.isTrue(StringUtils.isBlank(phone),"联系电话不能为空");
        //电话号码不能违法
        AssertUtil.isTrue(!(PhoneUtil.isMobile(phone)),"联系电话不合法");
    }

    //修改用户信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeUser(User user){
        //验证用户是否存在
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(null==temp,"待修改的用户记录不存在");
        //检查用户参数
        checkUser(user.getUserName(),user.getPhone(),user.getEmail());
        //修改用户名已存在问题
        User temp2 = userMapper.queryUserByUserName(user.getUserName());
        AssertUtil.isTrue(temp2!=null && !(temp2.getId().equals(user.getId())),"用户名已存在");
        //设置默认值
        user.setUpdateDate(new Date());
        //验证修改是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(user)<1,"修改失败");
        //批量操作中间表
        relationUserRole(user.getId(),user.getRoleIds());
    }

    //批量删除
    @Transactional(propagation = Propagation.REQUIRED)
    public void deletesUser(Integer[] ids){
        //验证
        AssertUtil.isTrue(ids==null || ids.length==0,"待删除的记录不存在");
        //删除已有角色
        for (Integer userId : ids) {
            //统计用户有多少角色
            int count = userRoleMapper.countUserRoleNum(userId);
            //删除已有角色
            if (count>0){
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"原角色信息删除失败");
            }
        }
        //验证删除是否成功
        AssertUtil.isTrue(deleteBatch(ids)<1,"删除失败");
    }
}
