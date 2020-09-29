package com.offcn.user.service.impl;

import com.offcn.user.enums.UserExceptionEnum;
import com.offcn.user.exception.UserException;
import com.offcn.user.mapper.TMemberAddressMapper;
import com.offcn.user.mapper.TMemberMapper;
import com.offcn.user.pojo.TMember;
import com.offcn.user.pojo.TMemberAddress;
import com.offcn.user.pojo.TMemberAddressExample;
import com.offcn.user.pojo.TMemberExample;
import com.offcn.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TMemberMapper memberMapper;

    @Autowired
    private TMemberAddressMapper memberAddressMapper;

    @Override
    public void registerUser(TMember member) {
        //1.检查系统中此手机号是否存在
        TMemberExample example = new TMemberExample();
        example.createCriteria().andLoginacctEqualTo(member.getLoginacct());
        long l = memberMapper.countByExample(example);
        if(l>0){
            //如果手机号已经存在，抛出自定义的异常
            throw new UserException(UserExceptionEnum.LOGINACCT_EXIST);
        }
        // 2、手机号未被注册，设置相关参数，保存注册信息
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //手机号没有被占用，先将密码加密
        String encode = encoder.encode(member.getUserpswd());
        //设置密码
        member.setUserpswd(encode);
        member.setUsername(member.getLoginacct());
        member.setEmail(member.getEmail());
        //实名认证状态 0 - 未实名认证， 1 - 实名认证申请中， 2 - 已实名认证
        member.setAuthstatus("0");
        //用户类型: 0 - 个人， 1 - 企业
        member.setUsertype("0");
        //账户类型: 0 - 企业， 1 - 个体， 2 - 个人， 3 - 政府
        member.setAccttype("2");
        System.out.println("插入数据:"+member.getLoginacct());
        //将数据插入
        memberMapper.insertSelective(member);
    }

    /**
     * 登录用户
     * @param username
     * @param password
     * @return
     */
    @Override
    public TMember login(String username, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        TMemberExample example = new TMemberExample();
        example.createCriteria().andLoginacctEqualTo(username);
        List<TMember> list = memberMapper.selectByExample(example);
        if(list!=null && list.size()==1){
            TMember member = list.get(0);
            //比对密码
            boolean matches = encoder.matches(password, member.getUserpswd());
            return matches?member:null;
        }
        return null;
    }

    /**
     * 根据用户id，获取用户信息
     * @param id
     * @return
     */
    @Override
    public TMember findTmemberById(Integer id) {
        return memberMapper.selectByPrimaryKey(id);
    }


    /**
     * 根据用户ID获取用户地址
     * @param memberId
     * @return
     * TMemberAddressMapper
     */
    @Override
    public List<TMemberAddress> addressList(Integer memberId) {
        TMemberAddressExample tMemberAddressExample = new TMemberAddressExample();
        tMemberAddressExample.createCriteria().andMemberidEqualTo(memberId);
        return memberAddressMapper.selectByExample(tMemberAddressExample);
    }





}
