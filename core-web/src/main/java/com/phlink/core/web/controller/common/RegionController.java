package com.phlink.core.web.controller.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.phlink.core.web.entity.City;
import com.phlink.core.web.entity.Country;
import com.phlink.core.web.entity.Province;
import com.phlink.core.web.mapper.CityMapper;
import com.phlink.core.web.mapper.CountryMapper;
import com.phlink.core.web.mapper.ProvinceMapper;
import com.phlink.core.web.service.CityService;
import com.phlink.core.web.service.CountryService;
import com.phlink.core.web.service.ProvinceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author wen
 */
@Api(tags = "行政区域接口")
@RequestMapping("/common/region")
@RestController
@Transactional
@Slf4j
public class RegionController {
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private CityService cityService;
    @Autowired
    private CountryService countryService;

    @ApiOperation(value = "省级行政地区", httpMethod = "GET")
    @GetMapping("/province")
    public List<Province> provinces(@RequestParam(required = false) String name) {
        return this.provinceService.listByCondition(name);
    }

    @ApiOperation(value = "市级行政地区", httpMethod = "GET")
    @GetMapping("/city")
    public List<City> cities(@Valid @RequestParam @NotBlank(message = "{required}") String provinceId) {
        return this.cityService.listByProvinceId(provinceId);
    }

    @ApiOperation(value = "县区级行政地区", httpMethod = "GET")
    @GetMapping("/country")
    public List<Country> countries(@Valid @RequestParam @NotBlank(message = "{required}") String cityId) {
        return this.countryService.listByCityId(cityId);
    }

}
