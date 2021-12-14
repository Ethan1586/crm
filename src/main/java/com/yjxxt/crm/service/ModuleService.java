package com.yjxxt.crm.service;

import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.controller.ModuleController;
import com.yjxxt.crm.dto.TreeDto;
import com.yjxxt.crm.mapper.ModuleMapper;
import com.yjxxt.crm.mapper.PermissionMapper;
import com.yjxxt.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {

    //注入
    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    //展示zTree
    public List<TreeDto> findModules(){
        return moduleMapper.selectModules();
    }

    //角色授权
    public List<TreeDto> queryAllModules(Integer roleId){
        //收集所有资源
        List<TreeDto> treeDtos = moduleMapper.selectModules();
        //根据角色id查询角色拥有的菜单id
        List<Integer> roleHasMids = permissionMapper.queryRoleHasAllModuleIdsByRoleId(roleId);
        //检验 如果当前角色分配了菜单
        if (null!=roleHasMids && roleHasMids.size()>0){
            treeDtos.forEach(treeDto -> {
                if (roleHasMids.contains(treeDto.getId())){
                    treeDto.setChecked(true);
                }
            });
        }
        return treeDtos;
    }

    //查询
    public Map<String,Object> moduleList(){
        //实力化map
        Map<String,Object> map = new HashMap<String,Object>();
        //获取module对象
        List<Module> mList = moduleMapper.queryModules();
        //设置默认值
        map.put("code",0);
        map.put("msg","success");
        map.put("count",mList.size());
        map.put("data",mList);
        //返回
        return map;
    }

    //添加资源目录
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveModule(Module module) {
        //菜单名不为空
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "菜单名不能为空");
        //菜单层级合法
        Integer grade = module.getGrade();
        AssertUtil.isTrue(null == grade || !(grade == 1 || grade == 2 || grade == 0), "菜单层级不合法");
        //菜单唯一
        AssertUtil.isTrue(null != moduleMapper.queryModuleByGradeAndModuleName(module.getGrade(), module.getModuleName()), "该层级下级菜单重复");
        if (grade == 1) {
            //二级菜单不能为空
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "请指定二级菜单url值");
            //二级菜单不能重复
            AssertUtil.isTrue(null != moduleMapper.queryModuleByGradeAndByUrl(module.getGrade(), module.getUrl()), "二级菜单url不可重复");
        }
        if (grade != 0) {
            Integer parentId = module.getParentId();
            AssertUtil.isTrue(null != parentId || null == selectByPrimaryKey(parentId), "请指定上级菜单");
        }
        //权限码不能为空
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输入权限码");
        //权限码唯一
        AssertUtil.isTrue(null!=moduleMapper.queryModuleByOptValue(module.getOptValue()),"权限码重复！");
        //设定默认值
        module.setIsValid((byte) 1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());
        //验证添加结果
        AssertUtil.isTrue(insertSelective(module)<1,"添加菜单失败");
    }

    //更新菜单目录
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module) {
        //id存在且唯一
        AssertUtil.isTrue(null==module.getId() || null==selectByPrimaryKey(module.getId()),"菜单不存在！");
        //菜单名不能为空
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "菜单名不能为空");
        //菜单层级合法
        Integer grade = module.getGrade();
        AssertUtil.isTrue(null == grade || !(grade == 1 || grade == 2 || grade == 3), "菜单层级不合法");
        //获取module
        Module temp = moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName());
        //菜单唯一
        if (null != temp) {
            //该层下级菜单已存在
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"该层下级菜单已存在");
        }
        if (grade == 1) {
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"请指定二级菜单url 值");
            temp =moduleMapper.queryModuleByGradeAndByUrl(grade,module.getUrl());
            if(null !=temp){
                AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"该层级下url已存在!");
            }
        }
        if(grade !=0){
            Integer parentId = module.getParentId();
            AssertUtil.isTrue(null==parentId || null==selectByPrimaryKey(parentId),"请指定上级菜单!");
        }
        //权限码不能为空
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输入权限码");
        //权限码唯一
        if(null !=temp){
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"权限码已存在!");
        }
        //设定默认值
        module.setUpdateDate(new Date());
        //验证添加结果
        AssertUtil.isTrue(updateByPrimaryKeySelective(module)<1,"添加菜单失败");
    }

    public List<Map<String,Object>> queryAllModulesByGrade(Integer grade){
        return moduleMapper.selectAllModuleByGrade(grade);
    }

    //删除菜单目录
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModuleById(Integer mid){
        Module temp =selectByPrimaryKey(mid);
        AssertUtil.isTrue(null == mid || null == temp,"待删除记录不存在!");
        //如果存在子菜单 不允许删除
        int count = moduleMapper.countSubModuleByParentId(mid);
        AssertUtil.isTrue(count>0,"存在子菜单，不支持删除操作!");
        //权限表
        count =permissionMapper.countPermissionsByModuleId(mid);
        if(count>0){
            AssertUtil.isTrue(permissionMapper.deletePermissionsByModuleId(mid)<count,"菜单删除失败!");
        }
        temp.setIsValid((byte) 0);
        AssertUtil.isTrue(updateByPrimaryKeySelective(temp)<1,"菜单删除失败!");
    }
}

