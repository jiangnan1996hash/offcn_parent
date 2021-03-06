package com.offcn.webui.service;

import com.offcn.response.AppResponse;
import com.offcn.webui.config.FeignConfig;
import com.offcn.webui.service.impl.MemberServiceFeignException;
import com.offcn.webui.vo.resp.UserAddressVo;
import com.offcn.webui.vo.resp.UserRespVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "SCW-USER",configuration = FeignConfig.class,fallback = MemberServiceFeignException.class)
public interface MemberServiceFeign {

    @PostMapping("/user/login")
    public AppResponse<UserRespVo> login(@RequestParam("username") String username, @RequestParam("password") String password);


    //根据用户编号，获取用户基本信息
    @GetMapping("/user/findUser/{id}")
    public AppResponse<UserRespVo> findUser(@PathVariable("id") Integer id);

    /**
     *  查询用户的地址列表
     * @param accessToken
     * @return
     */
    @GetMapping("/user/info/address")
    public AppResponse<List<UserAddressVo>> address(@RequestParam("accessToken") String accessToken);





}
