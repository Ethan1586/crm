package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {
    //多条件分页查询
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery){
        //实例化map对象
        Map<String,Object> map = new HashMap<>();
        //实例化分页单位
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        //开始分页
        PageInfo<SaleChance> pList = new PageInfo<SaleChance>(selectByParams(saleChanceQuery));
        //准备数据
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pList.getTotal());
        map.put("data",pList.getList());
        //返回结果
        return map;
    }

    //添加信息验证
    //开启事务
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance){
        //营销机会信息验证
        checkSaleChanceParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //状态
        //未分配
        if (StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }
        //分配
        if (StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(1);
            saleChance.setDevResult(1);  //分发中  2开发成功 3开发失败 0未开发
            saleChance.setAssignTime(new Date());
        }
        //分配时间
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        //添加是否成功
        AssertUtil.isTrue(insertSelective(saleChance)<1,"添加失败");
    }

    /**
     * 验证营销机会的添加
     * @param customerName 客户名
     * @param linkMan  联系人
     * @param linkPhone 联系电话
     */
    private void checkSaleChanceParams(String customerName, String linkMan, String linkPhone) {
        //客户名不能为空
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"客户名称不能为空");
        //联系人不能为空
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"联系人不能为空");
        //联系电话不能为空  且号码合法
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"联系电话不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"联系电话不合法");
    }

    //验证修改营销机会信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeSaleChance(SaleChance saleChance){
        //验证 获取营销机会信息
        SaleChance temp = selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(null==temp,"待修改的用户信息不存在");
        checkSaleChanceParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //未分配-->分配
        if (StringUtils.isBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        //分配-->未分配
        if (StringUtils.isNotBlank(temp.getAssignMan()) && StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(0);
            saleChance.setDevResult(0);
            saleChance.setAssignTime(null);
            saleChance.setAssignMan("");
        }
        //设定默认值
        saleChance.setUpdateDate(new Date());
        //修改是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(saleChance)<1,"修改失败");
    }

    //批量删除信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        //验证
        AssertUtil.isTrue(ids==null || ids.length==0,"请选择要删除的数据");
        //校验删除是否成功  要删除的数据应与选中的数据数量相同
        AssertUtil.isTrue(deleteBatch(ids)!=ids.length,"删除失败");
    }
}
