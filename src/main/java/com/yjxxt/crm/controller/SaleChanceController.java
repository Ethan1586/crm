package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.service.SaleChanceService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    //自动注入
    @Resource
    private SaleChanceService saleChanceService;

    @Resource
    private UserService userService;

    //营销机会管理界面
    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }

    //营销机会管理打开窗口界面
    @RequestMapping("addOrUpdateDialog")
    public String addOrUpdateDialog(Integer id, Model model){
        //判断
        if (null!=id){
            //获得营销机会信息
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            //存储信息
            model.addAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    //营销机会管理界面分页查询
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> sayList(SaleChanceQuery saleChanceQuery){
        //实例化结果信息
        Map<String, Object> map = saleChanceService.querySaleChanceByParams(saleChanceQuery);
        //返回json结果
        return map;
    }

    //添加营销机会信息
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(HttpServletRequest req, SaleChance saleChance){
        //获取用户id
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        //获取用户名
        String trueName = userService.selectByPrimaryKey(userId).getTrueName();
        //创建人
        saleChance.setCreateMan(trueName);
        //添加
        saleChanceService.addSaleChance(saleChance);
        //返回结果信息
        return success("添加成功");
    }

    //修改营销机会信息
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(HttpServletRequest req, SaleChance saleChance){
        //修改
        saleChanceService.changeSaleChance(saleChance);
        //返回结果信息
        return success("添加成功");
    }

    //批量删除营销机会信息
    @RequestMapping("dels")
    @ResponseBody
    public ResultInfo deletes(Integer[] ids){
        //删除
        saleChanceService.deleteSaleChance(ids);
        //返回结果信息
        return success("删除成功");
    }
}
