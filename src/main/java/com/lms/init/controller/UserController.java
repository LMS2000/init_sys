package com.lms.init.controller;




import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lms.contants.HttpCode;

import com.lms.init.annotation.AuthCheck;
import com.lms.init.constants.UserConstants;
import com.lms.init.entity.dao.User;
import com.lms.init.entity.dto.*;
import com.lms.init.entity.vo.LoginUserVo;
import com.lms.init.entity.vo.UserVo;
import com.lms.init.exception.BusinessException;
import com.lms.init.service.IUserService;
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

import static com.lms.init.constants.UserConstants.ENABLE;
import static com.lms.init.entity.factory.UserFactory.USER_CONVERTER;


@RestController
@RequestMapping("/user")
@EnableResponseAdvice
@Api(description = "用户管理")
public class UserController {

    @Resource
    private IUserService userService;


    /**
     * 登录
     * @param loginDto
     * @param request
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public LoginUserVo userLogin(@Validated @RequestBody LoginDto loginDto, HttpServletRequest request){


        try{
            //校验码校验
            String trueCode =(String)request.getSession().getAttribute(UserConstants.CHECK_CODE_KEY);
            String code = loginDto.getCode();
            BusinessException.throwIf(StringUtils.isEmpty(trueCode)||!trueCode.equals(code),HttpCode.PARAMS_ERROR,
                    "图片校验码不正确");
            return userService.userLogin(loginDto, request);
        }finally {
            request.getSession().removeAttribute(UserConstants.CHECK_CODE_KEY);
        }
    }




    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Boolean  userRegister(HttpSession session, @Validated @RequestBody RegisterUserDto registerUserDto){

        try{
            String code = (String) session.getAttribute(UserConstants.EMAIIL_HEADER);
            BusinessException.throwIf(StringUtils.isEmpty(code)||!code.equals(registerUserDto.getEmailCode()),
                    HttpCode.PARAMS_ERROR,"邮箱验证码错误");
            return userService.registerUser(registerUserDto);
        }finally {
            session.removeAttribute(UserConstants.EMAIIL_HEADER);
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
            session.setAttribute(UserConstants.CHECK_CODE_KEY,code);
        } else {
            session.setAttribute(UserConstants.CHECK_CODE_KEY_EMAIL,code);
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
            if (!code.equalsIgnoreCase((String) session.getAttribute(UserConstants.CHECK_CODE_KEY_EMAIL))) {
                throw new BusinessException(HttpCode.PARAMS_ERROR,"图片验证码不正确");
            }
            String emailCode = userService.sendEmail(email, type);
            session.setAttribute(UserConstants.EMAIIL_HEADER,emailCode);
            return true;
        } finally {
            session.removeAttribute(UserConstants.CHECK_CODE_KEY_EMAIL);
        }
    }


    /**
     * 注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Boolean logout(HttpServletRequest request){
        if (request == null) {
            throw new BusinessException(HttpCode.PARAMS_ERROR);
        }
        return userService.userLogout(request);
    }

    @GetMapping("/get/{id}")
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
    public UserVo getUserById(@PathVariable("id") Integer uid){
        User byId = userService.getById(uid);
        return USER_CONVERTER.toUserVo(byId);
    }


    /**
     * 获取当前用户
     * @param request
     * @return
     */
    @GetMapping(value = "/get/login")
    @ApiOperation("获取当前用户的登录信息")
    public LoginUserVo getCurrentUser(HttpServletRequest request){
        LoginUserVo loginUser = userService.getLoginUser(request);
        return loginUser;
    }




    /**
     * 添加用户
     *
     * @param userDto
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加用户")
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
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
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
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
            String code = (String) session.getAttribute(UserConstants.EMAIIL_HEADER);
            BusinessException.throwIf(StringUtils.isEmpty(code)||!code.equals(resetPasswordDto.getEmailCode()),
                    HttpCode.PARAMS_ERROR,"邮箱验证码错误");
            Long uid = userService.getLoginUser(request).getUid();
            return  userService.resetPassword(resetPasswordDto, uid);
        }finally {
            session.removeAttribute(UserConstants.EMAIIL_HEADER);
        }


    }

    /**
     * 上传头像
     *
     * @param file
     * @return 返回头像图片地址0
     */
    @PostMapping("/uploadAvatar")
    @ApiOperation("上次头像")
    public String uploadAvatar(@RequestBody MultipartFile file,HttpServletRequest request) {
        Long uid = userService.getLoginUser(request).getUid();
        return userService.uploadAvatar(file, uid);
    }


    /**
     * 分页条件获取用户列表
     * @param userPageDto
     * @return
     */
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
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
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
    @PostMapping("/change/enable")
    @ApiOperation("启用或者禁用用户")
    public Boolean changeUserEnable(@Validated @RequestBody ChangeUserEnableDto changeUserEnableDto) {
        Integer enable = changeUserEnableDto.getEnable();
        if (enable.equals(ENABLE)) {
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
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
    @ApiOperation("修改用户")
    public Boolean updateUser(@Validated @RequestBody UpdateUserDto userDto){
        return userService.updateUser(userDto);
    }

    @PostMapping("/update/current")
    @ApiOperation("修改当前用户")
    public Boolean updateCurrentUser(@Validated @RequestBody  UpdateCurrentUserDto userDto,HttpServletRequest request){
        return userService.updateCurrentUser(userDto,request);
    }


}