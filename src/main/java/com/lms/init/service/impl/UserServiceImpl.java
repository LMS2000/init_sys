package com.lms.init.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lms.contants.HttpCode;
import com.lms.init.constants.CommonConstant;
import com.lms.init.constants.EmailConstant;
import com.lms.init.constants.UserConstant;
import com.lms.init.event.SendEmailEvent;
import com.lms.init.model.dto.email.EmailMessage;
import com.lms.init.model.dto.email.SendEmailDto;
import com.lms.init.model.dto.user.*;
import com.lms.init.model.entity.User;
import com.lms.init.model.vo.UserVo;
import com.lms.init.exception.BusinessException;
import com.lms.init.mapper.UserMapper;
import com.lms.init.service.UserService;
import com.lms.init.utils.MybatisUtils;
import com.lms.init.utils.StringTools;
import com.lms.redis.RedisCache;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import javax.annotation.Resource;
import java.util.*;


import static com.lms.init.model.factory.UserFactory.USER_CONVERTER;


/**
 * @author lms2000
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;








    @Resource
    private RedisCache redisCache;



    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "luomosan";


    /**
     * 管理员添加用户
     * @param addUserDto
     * @return
     */
    @Override
    public Long addUser(AddUserDto addUserDto) {

        String username = addUserDto.getUsername();
        //校验重复的用户名
        BusinessException.throwIf(this.count(new QueryWrapper<User>().eq("username",username))>0,HttpCode.PARAMS_ERROR,"用户名重复");

        String password = addUserDto.getPassword();

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        User user=new User();
        BeanUtils.copyProperties(addUserDto,user);
        user.setPassword(encryptPassword);
        this.save(user);

        return user.getId();
    }

    /**
     * 用户注册
     * @param registerUserDto
     * @return
     */
    @Override
    public Boolean registerUser(RegisterUserDto registerUserDto) {

        String username = registerUserDto.getUsername();
        //校验重复的用户名
        BusinessException.throwIf(this.count(new QueryWrapper<User>().eq("username",username))>0,HttpCode.PARAMS_ERROR,"用户名重复");

        String password = registerUserDto.getPassword();

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        User user=new User();
        BeanUtils.copyProperties(registerUserDto,user);
        user.setUserRole(UserConstant.DEFAULT_ROLE);
        user.setNickname("user_"+ System.currentTimeMillis());
        user.setPassword(encryptPassword);
        return this.save(user);
    }

    @Override
    public UserVo userLogin(LoginDto loginDto) {
        // 1. 校验
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username",username)
                .eq("password",encryptPassword));
        // 用户不存在
        if (user == null) {
            throw new BusinessException(HttpCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        return USER_CONVERTER.toUserVo(user);

    }

    /**
     * 管理员修改用户
     * @param userDto
     * @return
     */
    @Override
    public Boolean updateUser(UpdateUserDto userDto) {
        Integer userId = userDto.getUid();
        //不可以修改超级管理员
        BusinessException.throwIfOperationAdmin(userId);
        BusinessException.throwIfNot(MybatisUtils.existCheck(this,Map.of("id",userId)));
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
       return this.updateById(user);
    }
    @Override
    public UserVo getUserById(Long uid) {
        User user = this.baseMapper.selectById(uid);
        if(ObjectUtils.isEmpty(user)){
            throw new BusinessException(HttpCode.PARAMS_ERROR,"找不到该用户");
        }
        return USER_CONVERTER.toUserVo(user);
    }

    /**
     * 分页获取
     * @param userPageDto
     * @return
     */
    @Override
    public Page<UserVo> pageUser(QueryUserPageDto userPageDto) {
        Integer enable = userPageDto.getEnable();
        String username = userPageDto.getUsername();
        Integer pageNum = userPageDto.getPageNum();
        Integer pageSize = userPageDto.getPageSize();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.like(StringUtils.isNotBlank(username), "username", userPageDto.getUsername())
                .eq(validEnable(enable), "is_enable", userPageDto.getEnable());
        Page<User> page = this.page(new Page<>(pageNum, pageSize), userQueryWrapper);
        List<User> records = page.getRecords();

        List<UserVo> userVos = USER_CONVERTER.toListUserVo(records);
        Page<UserVo> result = new Page<>(pageNum, pageSize, page.getTotal());
        result.setRecords(userVos);
        return result;
    }
    public boolean validEnable(Integer enable) {
        return ObjectUtils.isNotEmpty(enable) && (UserConstant.ENABLE.equals(enable) || UserConstant.DISABLE.equals(enable));
    }
    @Override
    public Boolean enableUser(Long id) {
        BusinessException.throwIf(id.equals(UserConstant.ADMIN_UID));
        Long count = this.baseMapper.selectCount(new QueryWrapper<User>().eq("id", id));
        BusinessException.throwIf(count<1,HttpCode.PARAMS_ERROR,"用户不存在");
        User user=new User();
        user.setId(id);
        user.setEnable(UserConstant.ENABLE);
        return updateById(user);
    }

    @Override
    public Boolean disableUser(Long id) {
        BusinessException.throwIf(id.equals(UserConstant.ADMIN_UID));
        Long count = this.baseMapper.selectCount(new QueryWrapper<User>().eq("id", id));
        BusinessException.throwIf(count<1,HttpCode.PARAMS_ERROR,"用户不存在");
        User user=new User();
        user.setId(id);
        user.setEnable(UserConstant.DISABLE);
        return updateById(user);
    }




    /**
     * 删除用户,使用户不可用，逻辑删除
     *
     * @param uids
     */
    @Override
    public Boolean deleteUser(List<Long> uids) {
        //不可以删除超级管理员
        BusinessException.throwIf(uids.contains(UserConstant.ADMIN_UID));
        //集合包括不存在的用户
        List<User> userIdList = this.list(new QueryWrapper<User>().in("id", uids));
        BusinessException.throwIf(userIdList.size() != uids.size());
        //还得删除用户角色表信息？
        return this.update(new UpdateWrapper<User>().in("id", uids));
    }

    @Override
    public Boolean resetPassword(ResetPasswordDto resetPasswordDto, Long uid) {
        String oldPassword = resetPasswordDto.getOldPassword();
        String newPassword = resetPasswordDto.getNewPassword();

        String encodeOldPassword =  DigestUtils.md5DigestAsHex((SALT + oldPassword).getBytes());
        //密码不对
        BusinessException
                .throwIfNot(MybatisUtils.existCheck(this, Map.of("id", uid,
                        "password", encodeOldPassword)), HttpCode.PARAMS_ERROR);

        //如果新旧密码一致就直接返回
        if (oldPassword.equals(newPassword)) {
            return false;
        }
        String encodeNewPassword =  DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes());
        User user =new User();
        user.setId(uid);
        user.setPassword(encodeNewPassword);
        return updateById(user);
    }

    @Override
    public void sendEmail(SendEmailDto sendEmailDto) {
        String email = sendEmailDto.getEmail();
        Integer type = sendEmailDto.getType();
        //如果是注册，校验邮箱是否已存在
        if (Objects.equals(type, CommonConstant.ZERO)) {
            com.lms.exception.BusinessException.throwIf(MybatisUtils.existCheck(this, Map.of("email", email)), HttpCode.PARAMS_ERROR,
                    "邮箱已占用");
        }
        //检查redis中是否有相同的邮箱
        boolean hasCode = redisCache.getCacheObjectOnlyRedis(EmailConstant.EMAIIL_HEADER + type + "_" + email) != null;

        com.lms.exception.BusinessException.throwIf(hasCode, HttpCode.PARAMS_ERROR, "重复发送邮件");
        EmailMessage emailMessage=new EmailMessage();
        BeanUtils.copyProperties(sendEmailDto,emailMessage);
        publishSendEmailEvent(emailMessage);
    }

    /**
     * 发布发送邮件事件
     * @param emailMessage
     */
    private void publishSendEmailEvent(EmailMessage emailMessage){
        SendEmailEvent sendEmailEvent = new SendEmailEvent(this, emailMessage);
        SpringUtil.publishEvent(sendEmailEvent);
    }

    /**
     * 修改当前用户信息
     * @param userDto
     * @return
     */
    @Override
    public Boolean updateCurrentUser(UpdateCurrentUserDto userDto, Long uid) {
        User user=new User();
        user.setNickname(userDto.getNickname());
        user.setEmail(userDto.getEmail());
        user.setId(uid);
        return this.updateById(user);
    }
}
