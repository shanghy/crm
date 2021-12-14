package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.SaleChance;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.PhoneUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {


    /**
     * 条件查询列表
     * code
     * msg
     * count
     * data
     */
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery) {
//        实例化Map
        Map<String, Object> map = new HashMap<String, Object>();
//        实例化分页单位
        PageHelper.startPage(saleChanceQuery.getPage(), saleChanceQuery.getLimit());
//        开始分页
        PageInfo<SaleChance> plist = new PageInfo<>(selectByParams(saleChanceQuery));
//        准备数据
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", plist.getTotal());
        map.put("data", plist.getList());

        return map;
    }

    /** 添加营销机会**/
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance) {

        /**
         * 营销机会数据添加
         * 1.参数校验
         * customerName:非空
         * linkMan:非空
         * linkPhone:非空 11位手机号
         * */
        checkSaleChanceParam(saleChance.getCustomerName(), saleChance.getLinkPhone(), saleChance.getLinkMan());
        /**
         * state:默认未分配-->0 如果选择分配人 state 为已分配--->1
         * assignTime:如果 如果选择分配人 时间为当前系统时间
         * devResult:默认未开发 如果选择分配人devResult为开发中 0-未开发 1-开发中 2-开发成功
         3-开发失败
         * isValid:默认有效数据(1-有效 0-无效)
         * createDate updateDate:默认当前系统时间
         * 3.执行添加 判断结果
         */
        //未分配
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }//已经分配
        if (StringUtils.isNotBlank(saleChance.getAssignMan())) {
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        saleChance.setCreateDate(new Date());
        saleChance.setIsValid(1);

        //添加是否成功
        AssertUtil.isTrue((insertSelective(saleChance) < 1), "修改失败");

    }

    /**
     * 验证
     * @param customerName 客户名称非空
     * @param linkMan      联系人非空
     * @param linkPhone    手机非空且合法
     */
    private void checkSaleChanceParam(String customerName, String linkPhone, String linkMan) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "请输入客户名称");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "请输入联系人");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "请输入联系人手机号");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone), "请输入合法的联系人手机号");
    }

    /** 修改  营销机会
     * 1.当前用户ID
     * 2.用户名非空
     * 3.联系人非空
     * 4.电话非空+合法
     * 5.设定默认值
     * 6.修改是否成功
     *
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeSaleChance(SaleChance saleChance) {
        //获取Id
        SaleChance temp = selectByPrimaryKey(saleChance.getId());
        //校验
        AssertUtil.isTrue(temp == null, "待修改记录不存在");
        checkSaleChanceParam(saleChance.getCustomerName(), saleChance.getLinkPhone(), saleChance.getLinkMan());

        if (StringUtils.isBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())) {
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }

        if ((StringUtils.isNotBlank(temp.getAssignMan())) && (StringUtils.isBlank(saleChance.getAssignMan()))) {
            saleChance.setState(0);
            saleChance.setDevResult(0);
            saleChance.setAssignTime(null);
            saleChance.setAssignMan("");
        }
        //设定默认值
        saleChance.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(saleChance) < 1, "修改失败");

    }

    /**
     * 营销机会删除
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids) {
        //判断要删除的id是否为空
        AssertUtil.isTrue(ids == null || ids.length == 0, "请选择要删除的数据!");
        //删除数据
        AssertUtil.isTrue((deleteBatch(ids) < 1) || (deleteBatch(ids) != ids.length), "删除失败");
    }

}
