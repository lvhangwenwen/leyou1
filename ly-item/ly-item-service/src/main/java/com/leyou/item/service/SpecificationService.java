package com.leyou.item.service;


import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper paramMapper;


    public List<SpecGroup> queryGroupBycid(Long cid) {

        SpecGroup specGroup=new SpecGroup();
        specGroup.setCid(cid);
        //根据group的非空字段查询
        List<SpecGroup> list = groupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(list)){
            throw  new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return list;
    }

    public List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching) {

        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> list = paramMapper.select(specParam);
        if (CollectionUtils.isEmpty(list)){
            throw  new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }

        return list;

    }

    public List<SpecGroup> queryListByCid(Long cid) {

        List<SpecGroup> specGroups = queryGroupBycid(cid);
        //查询分类内参数
        List<SpecParam> specParams = queryParamList(null, cid, null);

        //把规格参数变为map，map的key时规格组id，值是组下所有参数
        Map<Long,List<SpecParam>>map=new HashMap<>();

        for (SpecParam param : specParams) {
            if (!map.containsKey(param.getGroupId())){
                //这个组id在map不存在，新增
               map.put(param.getGroupId(),new ArrayList<>()) ;
            }
            map.get(param.getGroupId()).add(param);
        }


        //填充param到group

        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }
        return specGroups;
    }
}
