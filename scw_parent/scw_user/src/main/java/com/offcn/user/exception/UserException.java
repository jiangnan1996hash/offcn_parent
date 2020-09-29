package com.offcn.user.exception;


import com.offcn.user.enums.UserExceptionEnum;

/**
 * 自行编写异常
 */
public class UserException extends RuntimeException {


    /**
     * 单表所有用户异常
     * 邮箱
     * 用户名
     * 密码
     */
    public UserException(UserExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
    }


}
