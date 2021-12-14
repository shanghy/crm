package com.yjxxt.crm.dao;

import com.yjxxt.crm.base.BaseMapper;
import com.yjxxt.crm.bean.Module;
import com.yjxxt.crm.dto.TreeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ModuleMapper extends BaseMapper<Module, Integer> {

    public List<TreeDto> selectModules();

    List<Module> queryModules();

    Module queryModuleByGradeAndModuleName(Integer grade, String moduleName);

    Module queryModuleByGradeAndUrl(Integer grade, String url);

    Module queryModuleByOptValue(String optValue);

    int countSubModuleByParentId(Integer mid);

    List<Map<String, Object>> selectAllModuleByGrade(Integer grade);
}