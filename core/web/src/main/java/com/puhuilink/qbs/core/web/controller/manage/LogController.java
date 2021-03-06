/*
 * @Author: sevncz.wen
 * @Date: 2020-05-18 18:05:00
 * @Last Modified by: sevncz.wen
 * @Last Modified time: 2020-05-18 18:07:49
 */
package com.puhuilink.qbs.core.web.controller.manage;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.puhuilink.qbs.core.base.vo.PageVO;
import com.puhuilink.qbs.core.base.vo.Result;
import com.puhuilink.qbs.core.base.vo.SearchVO;
import com.puhuilink.qbs.core.web.entity.LogTrace;
import com.puhuilink.qbs.core.web.service.LogTraceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "日志管理接口")
@RequestMapping("/api/manage/log")
@Transactional
public class LogController {

    @Autowired
    private LogTraceService logService;

    @GetMapping(value = "/page")
    @ApiOperation(value = "分页获取全部")
    public Result page(@RequestParam(required = false) Integer type, @RequestParam String key,
                       SearchVO searchVo, PageVO pageVo) {
        PageInfo<LogTrace> page = PageHelper
                .startPage(pageVo.getPageNumber(), pageVo.getPageSize(), pageVo.getSort() + " " + pageVo.getOrder())
                .doSelectPageInfo(() -> logService.listByCondition(type, key, searchVo));
        return Result.ok().data(page);
    }

    // @DeleteMapping(value = "/{ids}")
    // @ApiOperation(value = "批量删除")
    // public String delByIds(@PathVariable String[] ids) {

    // for (String id : ids) {
    // logService.removeById(id);
    // }
    // return "删除成功";
    // }

    // @DeleteMapping(value = "/all")
    // @ApiOperation(value = "全部删除")
    // public String deleteAll() {
    // logService.removeAll();
    // return "删除成功";
    // }
}
