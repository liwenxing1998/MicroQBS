/*
 * @Author: sevncz.wen
 * @Date: 2020-05-06 14:52:42
 * @Last Modified by: sevncz.wen
 * @Last Modified time: 2020-05-18 18:13:29
 */
package com.phlink.core.web.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.phlink.core.web.entity.Province;
import com.phlink.core.web.mapper.ProvinceMapper;
import com.phlink.core.web.service.ProvinceService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.util.StrUtil;

@Service("provinceService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ProvinceServiceImpl extends ServiceImpl<ProvinceMapper, Province> implements ProvinceService {

    @Override
    public List<Province> listByCondition(String name) {
        LambdaQueryWrapper<Province> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like(Province::getName, name);
        }
        return list(queryWrapper);
    }
}