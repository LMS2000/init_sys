package com.lms.init.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.init.model.dto.email.SendEmailDto;
import com.lms.init.model.dto.user.*;
import com.lms.init.model.entity.User;
import com.lms.init.model.vo.UserVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author LMS2000
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param addUserDto
     * @return 新用户 id
     */
    Long addUser(AddUserDto addUserDto);


    Boolean registerUser(RegisterUserDto registerUserDto);




    /**
     * 用户登录
     *
     * @param loginDto
     * @return 脱敏后的用户信息
     */
    UserVo userLogin(LoginDto loginDto);

    Boolean updateUser(UpdateUserDto userDto);





    UserVo getUserById(Long uid);

//    /**
//     * 保存用户
//     * @param user
//     * @return
//     */
//     boolean save(User user);

    Page<UserVo> pageUser(QueryUserPageDto userPageDto);



    Boolean enableUser(Long id);
    Boolean disableUser(Long id);


    Boolean deleteUser(List<Long> uids);
    Boolean resetPassword(ResetPasswordDto resetPasswordDto, Long uid);
//
//
//    String uploadAvatar(MultipartFile file, Long uid);

    Boolean  updateCurrentUser(UpdateCurrentUserDto userDto, Long uid);
    void sendEmail(SendEmailDto sendEmailDto);
}
