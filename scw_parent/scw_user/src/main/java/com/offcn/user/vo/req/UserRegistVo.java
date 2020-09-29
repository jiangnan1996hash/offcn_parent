package com.offcn.user.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户注册提交的数据VO
 */
@ApiModel("用户注册提交的数据VO")
@Data
public class UserRegistVo {

    @ApiModelProperty(value = "用户的手机号")
    private String loginacct;

    @ApiModelProperty(value = "密码")
    private String userpswd;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("验证码")
    private String code;

}
