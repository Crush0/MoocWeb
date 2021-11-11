package cn.edu.just.moocweb.utils;

public class ErrCode {
    public static final Integer SUCCESS = 1000;
    public static final Integer UNKNOWN_ERR = 4444;

    //用户注册错误码
    public static final Integer USERNAME_INVALID = 5001;
    public static final Integer TWICE_PWD_NOT_MATCH = 5002;
    public static final Integer PASSWORD_INVALID = 5003;
    public static final Integer EMAIL_INVALID = 5004;
    public static final Integer USER_EXIST = 5005;
    public static final Integer EMAIL_EXIST = 5004;
    public static final Integer NOT_NULL = 5005;

    //用户登陆错误码
    public static final Integer USER_NOTFOUND = 4001;
    public static final Integer PWD_NOT_MATCH = 4002;
    public static final Integer USER_BAN = 4003;

    //MOOC账号相关
    public static final Integer BIND_ERROR = 6001;
}
