package com.offcn.webui.controller;


import com.alibaba.druid.util.StringUtils;
import com.offcn.response.AppResponse;
import com.offcn.webui.service.MemberServiceFeign;
import com.offcn.webui.service.ProjectServiceFeign;
import com.offcn.webui.vo.resp.ProjectVo;
import com.offcn.webui.vo.resp.UserRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Controller
@Slf4j
public class DispathcherController {

    @Autowired
    private MemberServiceFeign memberServiceFeign;

    @Autowired
    private ProjectServiceFeign projectServiceFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 打开页面直接到达index.html
     * @return
     */
    @RequestMapping("/")
    public String toIndex(Model model){
        //redis中读取项目集合
        List<ProjectVo> data = (List<ProjectVo>) redisTemplate.opsForValue().get("projectStr");
        //redis项目集合为空，就调用项目服务，读取全部项目
        if (data == null){
            AppResponse<List<ProjectVo>> allProject = projectServiceFeign.all();
            data = allProject.getData();
            redisTemplate.opsForValue().set("projectStr",data,1, TimeUnit.HOURS);
        }
        model.addAttribute("projectList",data);

        return "index";
    }


    @RequestMapping("/doLogin")
    public String doLogin(String loginacct, String password, HttpSession session){

        //调用远程用户服务
        AppResponse<UserRespVo> appResponse = memberServiceFeign.login(loginacct, password);
        //获取响应数据
        UserRespVo userRespVo = appResponse.getData();
        log.info("登录账号：{},密码{}",loginacct,password);
        log.info("登录用户的数据:{}",userRespVo);

        if(userRespVo==null){
            //账号不存在，登录 重定向
            return "redirect:/login.html";
        }
        //用户存在，登录成功，把用户信息存储到session
        session.setAttribute("sessionMember",userRespVo);

        //从session获取前缀
        String preUrl = (String) session.getAttribute("preUrl");

        //如果前缀不存在，跳转到默认首页
        if(StringUtils.isEmpty(preUrl)){
            return "redirect:/";
        }

        //如果存在就跳转到前缀地址
        return "redirect:/"+preUrl;

    }












}
