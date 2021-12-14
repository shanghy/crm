package com.yjxxt.crm.service;

import com.yjxxt.crm.base.BaseService;
import com.yjxxt.crm.bean.Permission;
import com.yjxxt.crm.dao.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService extends BaseService<Permission,Integer> {
    @Autowired
    PermissionMapper permissionMapper;


    public List<String> queryUserHasRolesHasPermissions(Integer userId) {
        return permissionMapper.queryUserHasRolesHasPermissions(userId);
    }

}
