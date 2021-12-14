package com.yjxxt.crm.dao;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {
    int countUserRoleNum(Integer id);

    int deleteUserRoleByUserId(Integer id);
}