package com.lms.init.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.init.entity.dao.User;
import com.lms.init.entity.dto.*;
import com.lms.init.entity.vo.LoginUserVo;
import com.lms.init.entity.vo.UserVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author LMS2000
 */
public interface IUserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param addUserDto
     * @return 新用户 id
     */
    Integer addUser(AddUserDto addUserDto);


    Boolean registerUser(RegisterUserDto registerUserDto);




    /**
     * 用户登录
     *
     * @param loginDto
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVo userLogin(LoginDto loginDto, HttpServletRequest request);

    Boolean updateUser(UpdateUserDto userDto);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    LoginUserVo getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

//    /**
//     * 保存用户
//     * @param user
//     * @return
//     */
//     boolean save(User user);

    Page<UserVo> pageUser(QueryUserPageDto userPageDto);



    Boolean enableUser(Integer id);
    Boolean disableUser(Integer id);


    Boolean deleteUser(List<Integer> uids);
    Boolean resetPassword(ResetPasswordDto resetPasswordDto, Integer uid);
//
//
    String uploadAvatar(MultipartFile file, Integer uid);

    Boolean  updateCurrentUser(UpdateCurrentUserDto userDto,HttpServletRequest request);
    String sendEmail(String email ,Integer type);
}
