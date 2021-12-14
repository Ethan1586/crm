package com.yjxxt.crm.mapper;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.dto.TreeDto;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ModuleMapper extends BaseMapper<Module,Integer> {
    //查找所有module
    public List<TreeDto> selectModules();

    //分页查询
    @MapKey("")
    public List<Module> queryModules();

    //根据菜单等级和名字查询菜单
    public Module queryModuleByGradeAndModuleName(@Param("grade") Integer grade, @Param("moduleName")String moduleName);

    //根据菜单等级和url查询菜单
    public Module queryModuleByGradeAndByUrl(Integer grade,String url);

    //根据菜单等级和url查询菜单
    public Module queryModuleByOptValue(String optValue);

    @MapKey("")
    List<Map<String, Object>> selectAllModuleByGrade(Integer grade);

    //查询子菜单数
    public Integer countSubModuleByParentId(Integer mid);
}