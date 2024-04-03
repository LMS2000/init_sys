package com.lms.init.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lms.contants.HttpCode;
import com.lms.init.client.OssClient;
import com.lms.init.config.OssProperties;
import com.lms.init.constants.CommonConstant;
import com.lms.init.constants.FileConstant;
import com.lms.init.constants.UserConstant;
import com.lms.init.model.dto.email.SysSettingsDto;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static com.lms.init.constants.FileConstant.STATIC_REQUEST_PREFIX;
import static com.lms.init.model.factory.UserFactory.USER_CONVERTER;


/**
 * @author lms2000
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;



    @Resource
    private JavaMailSender javaMailSender;


    @Resource
    private OssClient ossClient;



    @Resource
    private OssProperties ossProperties;

    /**
     * 发送人
     */
    @Value("${spring.mail.username}")
    private String sendUserName;

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
        //登录
        StpUtil.login(user.getId());
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
        if(ObjectUtils.isNotEmpty(user)){
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
                .eq(validEnable(enable), "enable", userPageDto.getEnable());
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

//    @Override
//    public String uploadAvatar(MultipartFile file, Long uid) {
//        //校验文件
//        validFile(file);
//        User user = this.getById(uid);
//        String bucketName = "bucket_user_" + uid;
//        if (!user.getAvatar().equals(FileConstant.DEFAULT_URL)) {
//            String[] split = user.getAvatar().split(bucketName);
//            ossClient.deleteObject(bucketName, split[1]);
//        }
//        //上传文件
//        String filePath;
//        try {
//            String randomPath =
//                    com.lms.init.utils.FileUtil.generatorFileName(file.getOriginalFilename() == null ? file.getName() : file.getOriginalFilename());
//            filePath = "avatar/" + randomPath;
//            ossClient.putObject(bucketName, filePath, file.getInputStream());
//
//        } catch (IOException e) {
//            throw new BusinessException(HttpCode.OPERATION_ERROR, "上传头像失败");
//        }
//
//        String fileUrl = com.lms.init.utils.FileUtil.getFileUrl(ossProperties.getEndpoint(), STATIC_REQUEST_PREFIX, bucketName, filePath);
//        this.updateById(User.builder().uid(uid).avatar(fileUrl).build());
//
//        return fileUrl;
//    }


    @Override
    public String sendEmail(String email, Integer type) {
        //如果是注册，校验邮箱是否已存在
        if (Objects.equals(type, CommonConstant.ZERO)) {
            BusinessException.throwIf(MybatisUtils.existCheck(this,Map.of("email",email)),HttpCode.PARAMS_ERROR,"邮箱被占用");
        }
        //随机的邮箱验证码
        String code = StringTools.getRandomNumber(CommonConstant.LENGTH_5);
        sendEmailCode(email, code);
        return code;
    }
    private void sendEmailCode(String toEmail, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //邮件发件人
            helper.setFrom(sendUserName);
            //邮件收件人 1或多个
            helper.setTo(toEmail);

            SysSettingsDto sysSettingsDto = new SysSettingsDto();

            //邮件主题
            helper.setSubject(sysSettingsDto.getRegisterEmailTitle());
            //邮件内容
            helper.setText(String.format(sysSettingsDto.getRegisterEmailContent(), code));
            //邮件发送时间
            helper.setSentDate(new Date());
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("邮件发送失败", e);
            throw new BusinessException(HttpCode.OPERATION_ERROR,"邮件发送失败");
        }
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
