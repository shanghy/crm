package com.yjxxt.crm.dao;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.Role;
import com.yjxxt.crm.query.RoleQuery;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleMapper  extends BaseMapper<Role,Integer> {

    //查询所有用户角色和对应的id
    @MapKey("")
    public List<Map<String,Object>> queryAllRoles(Integer userId);

    //新增
    public int insertSelective();

    //更新
    public int updateByPrimaryKeySelective();

    //根据角色名查询角色信息
   public Role queryRoleByRoleName(String roleName);
}