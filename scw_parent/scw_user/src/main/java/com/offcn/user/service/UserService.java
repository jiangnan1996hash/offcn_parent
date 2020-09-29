package com.offcn.user.service;


import com.offcn.user.pojo.TMember;
import com.offcn.user.pojo.TMemberAddress;

import java.util.List;

public interface UserService {

    /**
     * 注册
     * @param member
     */
    public void registerUser(TMember member);

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    public TMember login(String username,String password);


    /**
     *根据用户id，获取用户信息
     */
    public TMember findTmemberById(Integer id);


    /**
     * 获取用户收货地址
     * @param memberId
     * @return
     */
    List<TMemberAddress> addressList(Integer memberId);

}
