package com.yjxxt.crm.controller;

import com.yjxxt.crm.AccessController.RequirePermission;
import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.query.SaleChanceQuery;
import com.yjxxt.crm.service.PermissionService;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("main")
    public String main(HttpServletRequest req) {
        //获取cookie中当前用户id
        int userId = LoginUserUtil.releaseUserIdFromCookie(req);
        System.out.println(userId);
        //根据主键id获得用户对象
        User user = userService.selectByPrimaryKey(userId);
        System.out.println("---------------------------"+user);
        //存储
        req.setAttribute("user",user);
        List<String> permission = permissionService.queryUserHasRolesHasPermissions(userId);
        req.getSession().setAttribute("permissions",permission);
        //转发
        return "main";
    }

    @RequestMapping("welcome")
    public String welcome() {
        return "welcome";
    }



}
