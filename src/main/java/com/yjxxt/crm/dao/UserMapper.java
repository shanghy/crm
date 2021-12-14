package com.yjxxt.crm.dao;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.User;
import com.yjxxt.crm.query.UserQuery;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User, Integer> {


    User selectUserByName(String userName);


    @MapKey("")
    List<Map<String, Object>> queryAllSales();

}