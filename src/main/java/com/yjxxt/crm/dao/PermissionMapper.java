package com.yjxxt.crm.dao;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission,Integer> {

   public int countPermissionByRoleId(Integer roleId);

    List<Integer> queryRoleHasAllModuleIdsByRoleId(Integer roleId);

    //根据用户id查询用户权限
    List<String> queryUserHasRolesHasPermissions(Integer userId);

    int countPermissionsByModuleId(Integer mid);

    int deletePermissionsByModuleId(Integer mid);

    int deleteById(Integer deleteById);
}