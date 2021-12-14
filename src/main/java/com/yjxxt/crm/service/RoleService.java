package com.yjxxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Permission;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.dao.ModuleMapper;
import com.yjxxt.crm.dao.PermissionMapper;
import com.yjxxt.crm.dao.RoleMapper;
import com.yjxxt.crm.dto.TreeDto;
import com.yjxxt.crm.query.RoleQuery;
import com.yjxxt.crm.utils.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoleService extends BaseService<Role, Integer> {

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    PermissionMapper permissionMapper;

    @Autowired
    ModuleMapper moduleMapper;

    //    查询角色列表
    public List<Map<String, Object>> queryAllRoles(Integer userId) {
        return roleMapper.queryAllRoles(userId);
    }

    //动态查询角色表
    public Map<String, Object> querySaleByParams(RoleQuery roleQuery) {
//        实例化Map
        Map<String, Object> map = new HashMap<String, Object>();
//        实例化分页单位
        PageHelper.startPage(roleQuery.getPage(), roleQuery.getLimit());
//        开始分页
        PageInfo<Role> plist = new PageInfo<>(selectByParams(roleQuery));
//        准备数据
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", plist.getTotal());
        map.put("data", plist.getList());

        return map;
    }

    /**
     * 添加角色
     *
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveRole(Role role) {
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "请输入角色名");
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(temp != null, "角色名已经存在!");
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(roleMapper.insertSelective(role) < 1, "新增失败");
    }

    /**
     * 更新角色表信息
     *
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role) {
        //要更新的角色需要存在
        AssertUtil.isTrue(roleMapper.selectByPrimaryKey(role.getId()) == null, "待修改角色不存在");
        //角色名非空
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "请输入角色名");
        //需要修改的角色不能已经存在
        Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
        AssertUtil.isTrue(temp != null && !(temp.getId().equals(role.getId())), "角色已经存在");

        role.setUpdateDate(new Date());
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role) < 1, "修改失败");
    }

    /**
     * 根据roleId删除角色
     *
     * @param roleId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer roleId) {
        Role temp = roleMapper.selectByPrimaryKey(roleId);
        AssertUtil.isTrue(temp == null || roleId == null, "请选择要删除的角色");
        temp.setIsValid(0);
        AssertUtil.isTrue(updateByPrimaryKeySelective(temp) < 1, "删除失败");
    }

    /**
     * 权限记录添加
     * 核心表-t_permission t_role(校验角色存在)
     * 如果角色存在原始权限 删除角色原始权限
     * 然后添加角色新的权限 批量添加权限记录到t_permission
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer[] mids, Integer roleId) {
        //授权ID非空且在数据库中存在
        Role temp = selectByPrimaryKey(roleId);
        AssertUtil.isTrue(temp == null || roleId == null, "待修改权限的角色不存在或者没有选中");
        //根据roleId查询t_permission表中对应的module_id
        int count = permissionMapper.countPermissionByRoleId(roleId);
        //删除查询出的表中数据
        if (count > 0) {
            AssertUtil.isTrue(permissionMapper.deleteById(roleId) < 1, "删除失败");
        }
        //判断添加的moduleIds不为空
        if (mids != null && mids.length != 0) {
            //执行添加操作s
            List<Permission> permissionList = new ArrayList<>();
            for (Integer mid : mids) {
                Permission permission = new Permission();
                permission.setRoleId(roleId);
                permission.setModuleId(mid);
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                // 1.根据前端传输进来的权限id获得module对象
                // 2.再获得权限码
                // 3.通过set方法将权限码赋值给permission对象的AclValue属性
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());

                // 将设置好属性的permission对象放在创建好的数组中方便insert
                permissionList.add(permission);
            }
            //执行添加数据库添加操作,将数组中的数据添加到t_permission表中
            AssertUtil.isTrue(permissionMapper.insertBatch(permissionList) < permissionList.size(), "修改权限失败");

        }
    }



}
