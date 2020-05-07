package com.phlink.core.web.controller.noauth;

import javax.validation.Valid;

import com.phlink.core.base.annotation.SystemLogTrace;
import com.phlink.core.base.constant.CommonConstant;
import com.phlink.core.base.enums.LogType;
import com.phlink.core.base.validation.tag.OnAdd;
import com.phlink.core.web.controller.vo.UserRegistVO;
import com.phlink.core.web.entity.User;
import com.phlink.core.web.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "用户账户相关接口")
@RequestMapping("/api/auth/account")
@Transactional
public class UserAccountController {

    @Autowired
    private UserService userService;

    @Validated({ OnAdd.class })
    @PostMapping("/regist")
    @ApiOperation(value = "注册用户")
    @SystemLogTrace(description = "用户注册", type = LogType.OPERATION)
    public User regist(
            @RequestBody @Valid @ApiParam(name = "用户注册表单", value = "传入json格式", required = true) UserRegistVO userRegistVo) {
        User u = new User();
        String encryptPass = new BCryptPasswordEncoder().encode(userRegistVo.getPassword());
        u.setPassword(encryptPass);
        u.setType(CommonConstant.USER_TYPE_NORMAL);
        u.setUsername(userRegistVo.getUsername());
        u.setEmail(userRegistVo.getEmail());
        u.setMobile(userRegistVo.getMobile());
        u.setRealname(userRegistVo.getRealname());
        userService.save(u);
        return u;
    }

}
