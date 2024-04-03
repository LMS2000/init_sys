package com.lms.init.controller;




import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.contants.HttpCode;


import com.lms.init.constants.UserConstant;
import com.lms.init.model.dto.email.SendEmailDto;
import com.lms.init.model.dto.user.*;
import com.lms.init.model.entity.User;
import com.lms.init.model.vo.UserVo;
import com.lms.init.exception.BusinessException;
import com.lms.init.service.UserService;
import com.lms.init.utils.CreateImageCode;
import com.lms.result.EnableResponseAdvice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.lms.init.model.factory.UserFactory.USER_CONVERTER;


@RestController
@RequestMapping("/user")
@EnableResponseAdvice
@Api(description = "用户管理")
public class UserController {

    @Resource
    private UserService userService;


    /**
     * 登录
     * @param loginDto
     * @param request
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public UserVo userLogin(@Validated @RequestBody LoginDto loginDto, HttpServletRequest request){


        try{
            //校验码校验
            String trueCode =(String)request.getSession().getAttribute(UserConstant.CHECK_CODE_KEY);
            String code = loginDto.getCode();
            BusinessException.throwIf(StringUtils.isEmpty(trueCode)||!trueCode.equals(code),HttpCode.PARAMS_ERROR,
                    "图片校验码不正确");
            UserVo userVo = userService.userLogin(loginDto);
            StpUtil.login(userVo.getId());
            return userVo;
        }finally {
            request.getSession().removeAttribute(UserConstant.CHECK_CODE_KEY);
        }
    }




    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Boolean  userRegister(HttpSession session, @Validated @RequestBody RegisterUserDto registerUserDto){

        try{
            String code = (String) session.getAttribute(UserConstant.EMAIIL_HEADER);
            BusinessException.throwIf(StringUtils.isEmpty(code)||!code.equals(registerUserDto.getEmailCode()),
                    HttpCode.PARAMS_ERROR,"邮箱验证码错误");
            return userService.registerUser(registerUserDto);
        }finally {
            session.removeAttribute(UserConstant.EMAIIL_HEADER);
        }
    }

    @GetMapping(value = "/checkCode")
    @ApiOperation("图片校验码")
    public void checkCode(HttpServletResponse response, HttpServletRequest request, Integer type) throws
            IOException {
        CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = vCode.getCode();
        HttpSession session = request.getSession();
        if (type == null || type == 0) {
            session.setAttribute(UserConstant.CHECK_CODE_KEY,code);
        } else {
            session.setAttribute(UserConstant.CHECK_CODE_KEY_EMAIL,code);
        }
        vCode.write(response.getOutputStream());
    }

    /**
     * type 0 为注册 1 为找回密码
     * @param session
     * @return
     */
    @PostMapping("/sendEmailCode")
    @ApiOperation("邮箱验证码")
    public Boolean sendEmailCode(HttpSession session,@Validated @RequestBody SendEmailDto sendEmailDto) {
        String code = sendEmailDto.getCode();
        String email = sendEmailDto.getEmail();
        Integer type = sendEmailDto.getType();
        try {
            if (!code.equalsIgnoreCase((String) session.getAttribute(UserConstant.CHECK_CODE_KEY_EMAIL))) {
                throw new BusinessException(HttpCode.PARAMS_ERROR,"图片验证码不正确");
            }
            String emailCode = userService.sendEmail(email, type);
            session.setAttribute(UserConstant.EMAIIL_HEADER,emailCode);
            return true;
        } finally {
            session.removeAttribute(UserConstant.CHECK_CODE_KEY_EMAIL);
        }
    }


    /**
     * 注销
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Boolean logout(){
        StpUtil.logout();
        return true;
    }

    @GetMapping("/get/{id}")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public UserVo getUserById(@PathVariable("id") Integer uid){
        User byId = userService.getById(uid);
        return USER_CONVERTER.toUserVo(byId);
    }


    /**
     * 获取当前用户
     * @return
     */
    @GetMapping(value = "/get/login")
    @ApiOperation("获取当前用户的登录信息")
    @SaCheckLogin
    public UserVo getCurrentUser(){
        Long loginId = Long.parseLong((String)StpUtil.getLoginId());
        return userService.getUserById(loginId);
    }




    /**
     * 添加用户
     *
     * @param userDto
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加用户")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public Long add(@Validated @RequestBody(required = true) AddUserDto userDto) {
        return userService.addUser(userDto);
    }

    /**
     * 删除用户
     * @param userIds
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation("批量删除用户")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public Boolean removeUser( @RequestParam("userIds") List<Long> userIds){
        return userService.deleteUser(userIds);
    }





    /**
     * 修改当前用户的密码
     * @param resetPasswordDto
     * @return
     */
    @PostMapping("/resetPassword")
    @ApiOperation("修改密码")
    public Boolean resetPassword(@RequestBody ResetPasswordDto resetPasswordDto, HttpServletRequest request) {
        HttpSession session = request.getSession();
        try{
            String code = (String) session.getAttribute(UserConstant.EMAIIL_HEADER);
            BusinessException.throwIf(StringUtils.isEmpty(code)||!code.equals(resetPasswordDto.getEmailCode()),
                    HttpCode.PARAMS_ERROR,"邮箱验证码错误");
            Long uid = Long.parseLong((String) StpUtil.getLoginId());
            return  userService.resetPassword(resetPasswordDto, uid);
        }finally {
            session.removeAttribute(UserConstant.EMAIIL_HEADER);
        }
    }


    /**
     * 分页条件获取用户列表
     * @param userPageDto
     * @return
     */
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @PostMapping("/page")
    @ApiOperation("分页条件获取用户列表")
    public Page<UserVo> getUserPage(@RequestBody QueryUserPageDto userPageDto) {
        Page<UserVo> userVoPage = userService.pageUser(userPageDto);
        return userVoPage;
    }


    /**
     * 启用或者禁用用户
     * @param changeUserEnableDto
     * @return
     */
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @PostMapping("/change/enable")
    @ApiOperation("启用或者禁用用户")
    public Boolean changeUserEnable(@Validated @RequestBody ChangeUserEnableDto changeUserEnableDto) {
        Integer enable = changeUserEnableDto.getEnable();
        if (enable.equals(UserConstant.ENABLE)) {
            return userService.enableUser(changeUserEnableDto.getUid());
        } else {
            return userService.disableUser(changeUserEnableDto.getUid());
        }
    }

    /**
     * 修改用户
     * @param userDto
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    @ApiOperation("修改用户")
    public Boolean updateUser(@Validated @RequestBody UpdateUserDto userDto){
        return userService.updateUser(userDto);
    }

    @PostMapping("/update/current")
    @ApiOperation("修改当前用户")
    public Boolean updateCurrentUser(@Validated @RequestBody UpdateCurrentUserDto userDto){
        Long uid = Long.parseLong((String) StpUtil.getLoginId());
        return userService.updateCurrentUser(userDto,uid);
    }


}