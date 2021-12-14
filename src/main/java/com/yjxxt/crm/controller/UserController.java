package com.yjxxt.crm.controller;

import com.yjxxt.crm.base.BaseController;
import com.yjxxt.crm.base.ResultInfo;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.service.UserService;
import com.yjxxt.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @RequestMapping("login")
    @ResponseBody
    public ResultInfo say(User user) {
        System.out.println(user);
        ResultInfo resultInfo = new ResultInfo();
        UserModel userModel = userService.userLogin(user.getUserName(), user.getUserPwd());
        resultInfo.setResult(userModel);
        return resultInfo;
    }


    @RequestMapping("toPasswordPage")
    public String updatePwd() {
        return "user/password";
    }


    @RequestMapping("toSettingPage")
    public String setting(HttpServletRequest request) {
        Integer userid = LoginUserUtil.releaseUserIdFromCookie(request);
        User user = userService.selectByPrimaryKey(userid);
        request.setAttribute("user",user);
        return "user/setting";
    }


    @PostMapping("updatePwd")
    @ResponseBody
    public ResultInfo updatePwd(HttpServletRequest res, String oldPwd, String newPwd, String confirmPwd) {
        ResultInfo resultInfo = new ResultInfo();
        //获取cookie中用户id
        int userId = LoginUserUtil.releaseUserIdFromCookie(res);
        //修改密码操作
        userService.changeUserPwd(userId, oldPwd, newPwd, confirmPwd);
        return resultInfo;
    }

    @PostMapping("setting")
    @ResponseBody
    public ResultInfo sayUpdate(User user) {
        ResultInfo resultInfo = new ResultInfo();
        //修改信息操作
        userService.updateByPrimaryKeySelective(user);
        return resultInfo;
    }

    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String, Object>> queryAllSales(){
        List<Map<String, Object>> maps = userService.queryAllSales();
        return maps;
    }

    @RequestMapping("index")
    public String index(){
        return "/user/user";
    }

    /**
     * 多条件查询用户数据
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryUserByParams(UserQuery userQuery){
        return userService.queryUserByParams(userQuery);
    }

    //更新或者添加
    @RequestMapping("addOrUpdateUserPage")
    public String addOrUpdateUserPage(Integer id , Model model){
        if (id!=null){
            model.addAttribute("user",userService.selectByPrimaryKey(id));
        }
        return"user/add_update";
    }

    //添加用户控制器
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo saveUser(User user){
        userService.saveUser(user);
        return success("用户添加成功");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(User user) {
        //用户的添加
        userService.changeUser(user);
        //返回目标数据对象
        return success("用户修改OK");
    }
    //批量删除数据
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){

        userService.deleteUserByIds(ids);
        return success("删除成功");
    }
}
