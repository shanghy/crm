package com.yjxxt.crm.controller;

import com.yjxxt.crm.AccessController.RequirePermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {

    @Autowired
    RoleService roleService;

    //查询所有角色
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){

        return roleService.queryAllRoles(userId);
    }

    @RequestMapping("index")
    public String index(){
        return "/role/role";
    }

    @RequestMapping("list")
    @ResponseBody

    public Map<String, Object> queryAllParams(RoleQuery roleQuery){

        Map<String, Object> map = roleService.querySaleByParams(roleQuery);

        return  map;
    }

    @RequestMapping("toAddOrUpdate")
    public String toAddOrUpdate(Model model, Integer roleId){

        if (roleId!=null){
            Role role = roleService.selectByPrimaryKey(roleId);
            model.addAttribute("role",role);
            System.out.println(role);
        }

        return"role/add_update";
    }

    @RequestMapping("save")
    @ResponseBody
    public ResultInfo save(Role role){
        roleService.saveRole(role);
        return success("新增成功");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(Role role){
        roleService.updateRole(role);
        return success("修改成功");
    }

    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer id){
        roleService.deleteRole(id);

        return success("删除角色成功");
    }

    //角色模块授权
    @RequestMapping("toRoleGrantPage")
    public String toRoleGrantPage(Integer roleId,Model model){
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }

    //角色权限添加
    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer[] mids,Integer roleId){
        roleService.addGrant(mids,roleId);
        return success("添加权限成功");
    }



}
