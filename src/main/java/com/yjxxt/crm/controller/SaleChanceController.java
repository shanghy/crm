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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Autowired
    SaleChanceService saleChanceService;

    @Autowired
    UserService userService;

    @RequestMapping("index")
    public String index() {
        return "saleChance/sale_chance";
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> saylist(SaleChanceQuery saleChanceQuery) {
        //调佣方法获取数据
        Map<String, Object> stringObjectMap = saleChanceService.querySaleChanceByParams(saleChanceQuery);
        return stringObjectMap;
    }

    /**
     * 机会数据添加与更新页面视图转发
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateDialog")
    public String addOrUpdateSaleChancePage(Integer id, Model model) {
        //判断id是否为空,  修改or添加
        if (id!=null){
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            //储存
            model.addAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(HttpServletRequest request, SaleChance saleChance) {
        //设置创建人
        int userid = LoginUserUtil.releaseUserIdFromCookie(request);

        String trueName = userService.selectByPrimaryKey(userid).getTrueName();

        saleChance.setCreateMan(trueName);
        //添加
        saleChanceService.addSaleChance(saleChance);

        return success("添加成功了!");
    }


    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(SaleChance saleChance) {

        saleChanceService.changeSaleChance(saleChance);

        return success("修改成功了!");
    }

    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        saleChanceService.deleteSaleChance(ids);
        return success("营销机会数据删除成功!!!");
    }
}
