package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.bean.UserRole;
import com.yjxxt.crm.dao.UserMapper;
import com.yjxxt.crm.dao.UserRoleMapper;
import com.yjxxt.crm.model.UserModel;
import com.yjxxt.crm.query.UserQuery;
import com.yjxxt.crm.utils.AssertUtil;
import com.yjxxt.crm.utils.Md5Util;
import com.yjxxt.crm.utils.PhoneUtil;
import com.yjxxt.crm.utils.UserIDBase64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User, Integer> {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    //登陆方法
    public UserModel userLogin(String userName, String userPwd) {
        checkUserLoginParam(userName, userPwd);
        //用户是否存在
        User temp = userMapper.selectUserByName(userName);
        AssertUtil.isTrue(temp == null, "用户不存在");
        //校验密码
        checkUserPwd(userPwd,temp.getUserPwd());
        //构建返回对象
        return builderUserInfo(temp);
    }

    public UserModel builderUserInfo(User user) {
        //实例化
        UserModel userModel = new UserModel();
        //获取加密的id
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    //校验密码和用户名非空
    public void checkUserLoginParam(String userName, String userPwd) {
        //用户非空
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空");
        //密码非空
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "密码不能为空");
    }

    //校验密码和数据库密码
    public void checkUserPwd(String userPwd, String userPwd1) {
        //对输入密码加密
        userPwd = Md5Util.encode(userPwd);
        //输入密码和数据库密码对比
        AssertUtil.isTrue(!userPwd.equals(userPwd1), "用户密码不正确");
    }

    //修改用户密码
    public void changeUserPwd(Integer userId,String oldPassword,String newPassword,String confirmPwd){
        //根据cookie中的id获取对应用户对象
        User user = userMapper.selectByPrimaryKey(userId);
        //密码验证
        checkPasswordParams(user,oldPassword,newPassword,confirmPwd);

        user.setUserPwd(Md5Util.encode(newPassword));

        //确认密码修改是否成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改失败");
    }

    public void checkPasswordParams(User user, String oldPassword, String newPassword, String confirmPwd) {

        AssertUtil.isTrue(user==null,"用户未登录或者不存在");

        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"请输入原始密码");

        AssertUtil.isTrue(!(user.getUserPwd().equals(Md5Util.encode(oldPassword))), "原始密码不正确！");

        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"新密码不能为空");

        AssertUtil.isTrue((newPassword.equals(oldPassword)),"新密码和原始密码不能相同");

        AssertUtil.isTrue(StringUtils.isBlank(confirmPwd),"确认密码不能为空");

        AssertUtil.isTrue(!(confirmPwd.equals(newPassword)),"确认密码和新密码不一致");

    }

    //查询所有销售人员
    public List<Map<String,Object>> queryAllSales(){

        return userMapper.queryAllSales();

    }

    /**
     * 多条件分页查询用户数据
     * @param query
     * @return
     */
    public Map<String,Object>queryUserByParams(UserQuery query) {
        HashMap<String, Object> map = new HashMap<>();
        PageHelper.startPage(query.getPage(), query.getLimit());
        PageInfo<User> pageInfo = new PageInfo<>(userMapper.selectByParams(query));
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return  map;
    }

    /**
     * 添加用户
     * 1. 参数校验
     * 用户名 非空 唯一性
     * 邮箱 非空
     * 手机号 非空 格式合法
     * 2. 设置默认参数
     * isValid 1
     * creteDate 当前时间
     * updateDate 当前时间
     * userPwd 123456 -> md5加密
     * 3. 执行添加，判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void  saveUser(User user){
        checkUserSaveParam(user.getUserName(),user.getEmail(),user.getPhone());
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        AssertUtil.isTrue(userMapper.insertSelective(user)<1,"添加失败");
        System.out.println(user.getId()+"---"+user.getRoleIds());
        //操作中间表
        relaionUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 操作中间表
     * @param id  用户id
     * @param roleIds 角色id
     *      用户是否有角色
     *      有几个角色
     *       增加角色
     *       减少角色
     *       没有角色
     */
    public void relaionUserRole(Integer id, String roleIds) {
        //准备集合储存对象
        List<UserRole> urlist = new ArrayList<>();

        AssertUtil.isTrue(StringUtils.isBlank(roleIds),"请选择角色信息");
        System.out.println(id);
        //统计当前用户角色的数量
        int count = userRoleMapper.countUserRoleNum(id);
        if(count>0){
            //删除所有原来的用户角色
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(id)!=count,"用户角色删除失败");
        }
        //分割字符串
        String[] strings = roleIds.split(",");
        for(String rid : strings){
            //准备对象
            UserRole userRole = new UserRole();
            userRole.setUserId(id);
            userRole.setRoleId(Integer.parseInt(rid));
            userRole.setCreateDate(new Date());
            userRole.setUpdateDate(new Date());
            //存放到集合
            urlist.add(userRole);
        }
        AssertUtil.isTrue(userRoleMapper.insertBatch(urlist)!=urlist.size(),"用户角色分配失败");
    }

    //校验
    public void checkUserSaveParam(String userName, String email,String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"请输入用户名");
        // 验证用户名是否存在
        AssertUtil.isTrue(userMapper.selectUserByName(userName)!=null,"用户名已经存在");
        AssertUtil.isTrue(StringUtils.isBlank(email),"请输入邮箱");
        AssertUtil.isTrue(StringUtils.isBlank(phone),"请输入手机号");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"请输入合法手机号");
    }

    /** 更新
     * 一。验证：
     * 当前用户的ID 有否则不能修改
     * 修改用户名已经存在问题
     * 1：用户非空，且唯一
     * 2:邮箱 非空，
     * 3:手机号非空，格式正确
     * 二。设定默认值
     * is_valid=1
     * updateDate 系统时间
     * <p>
     * 三：修改是否成功
     * 修改用户角色
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeUser (User user){
        User user1 = userMapper.selectByPrimaryKey(user.getId());
        User user2Name = userMapper.selectUserByName(user.getUserName());
        AssertUtil.isTrue(user.getId()==null||user1==null,"需要修改的用户不存在!");
        AssertUtil.isTrue(!user.getId().equals(user1.getId())&& user2Name!=null,"用户名已经存在");
        AssertUtil.isTrue(user.getEmail()==null,"邮箱地址不能为空");
        AssertUtil.isTrue(user.getPhone()==null,"手机号码不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(user.getPhone()),"请输入正确格式的手机号");
        user.setUpdateDate(new Date());
        user.setIsValid(1);
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改失败!");
        relaionUserRole(user.getId(),user.getRoleIds());
    }
    //批量删除
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserByIds(Integer[] ids){
        AssertUtil.isTrue(null==ids || ids.length==0,"请选择要删除的数据");
        AssertUtil.isTrue(userMapper.deleteBatch(ids)<ids.length,"删除数据失败");
    }

}
