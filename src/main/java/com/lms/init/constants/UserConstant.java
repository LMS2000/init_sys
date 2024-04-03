package com.lms.init.constants;

/**
 * 用户常量
 *
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 系统用户 id（虚拟用户）
     */
    long SYSTEM_USER_ID = 0;

    //  region 权限

    /**
     * 默认权限
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员权限
     */
    String ADMIN_ROLE = "admin";
    Long ADMIN_UID=1L;
    //禁用标记
    Integer DISABLE=1;
    Integer ENABLE=0;

    //删除标记
    Integer DELETED=1;
    Integer NOT_DELETED=0;
    //用户初始密码
    String  INITPASSWORD="12345678";
    String CHECK_CODE_KEY = "check_code_key";
    String CHECK_CODE_KEY_EMAIL = "check_code_key_email";

    // 邮箱验证码前缀

    String EMAIIL_HEADER="check_email_code:";
    // endregion
}
