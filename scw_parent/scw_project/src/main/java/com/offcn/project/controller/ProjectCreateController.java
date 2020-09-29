package com.offcn.project.controller;

import com.alibaba.fastjson.JSON;
import com.offcn.project.contants.ProjectConstant;
import com.offcn.project.enums.ProjectStatusEnume;
import com.offcn.project.pojo.TReturn;
import com.offcn.project.service.ProjectCreateService;

import com.offcn.project.vo.req.ProjectBaseInfoVo;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import com.offcn.project.vo.req.ProjectReturnVo;
import com.offcn.response.AppResponse;

import com.offcn.vo.BaseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Api( tags ="项目基本功能模块（创建、保存、项目信息获取、文件上传等）")
@RestController
@Slf4j
@RequestMapping("/project")
public class ProjectCreateController  {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProjectCreateService projectCreateService;

    @ApiOperation("项目发起第1步-阅读同意协议")
    @GetMapping("/init")
    public AppResponse<String> init(BaseVo vo){
        String accessToken = vo.getAccessToken();
        //通过登录令牌获取项目ID
        String memberId = stringRedisTemplate.opsForValue().get(accessToken);
        if(StringUtils.isEmpty(memberId)){
            return AppResponse.fail("无此权限，请先登录");
        }

        //转换数据
        int id = Integer.parseInt(memberId);
        //保存临时项目信息到redis
        String projectToken = projectCreateService.initCreateProject(id);

        return AppResponse.ok(projectToken);
    }

    @ApiOperation("项目发起第2步-保存项目的基本信息")
    @PostMapping("/savebaseInfo")
    public AppResponse<String> savebaseInfo(ProjectBaseInfoVo projectBaseInfoVo){

        //取出redis中之前存储的json结构的项目信息
        String orignal = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX
                + projectBaseInfoVo.getProjectToken());

        //将json结构的项目信息转换成ProjectRedisStorageVo对象
        ProjectRedisStorageVo projectRedisStorageVo = JSON.parseObject(orignal, ProjectRedisStorageVo.class);

        //将页面接收的参数传递到projectRedisStorageVo中
        BeanUtils.copyProperties(projectBaseInfoVo,projectRedisStorageVo);

        //4、将这个Vo对象再转换为json字符串
        String jsonString = JSON.toJSONString(projectRedisStorageVo);
        //5、重新更新到redis
        stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX + projectBaseInfoVo.getProjectToken(), jsonString);

        return AppResponse.ok("ok");
    }

    @ApiOperation("项目发起第3步-项目保存项目回报信息")
    @PostMapping("/savereturn")
    public AppResponse<Object> saveReturnInfo(@RequestBody List<ProjectReturnVo> pro) {
        ProjectReturnVo projectReturnVo = pro.get(0);
        //取出项目的令牌
        String projectToken = projectReturnVo.getProjectToken();
        //使用令牌从缓存中取出项目信息
        String projectContext  = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);
        //将json转换回
        ProjectRedisStorageVo projectRedisStorageVo = JSON.parseObject(projectContext, ProjectRedisStorageVo.class);

        //将页面收集来的回报数据封装重新放入redis
        List<TReturn> list = new ArrayList<>();
        for(ProjectReturnVo vo:pro){
            TReturn tReturn = new TReturn();
            BeanUtils.copyProperties(vo, tReturn);
            list.add(tReturn);
        }

        //更新集合信息
        projectRedisStorageVo.setProjectReturns(list);

        //重新放入缓存中
        String jsonString = JSON.toJSONString(projectRedisStorageVo);
        stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken, jsonString);
        return AppResponse.ok("OK");
    }


    /**
     *
     * @param accessToken  用户的令牌
     * @param projectToken 项目的令牌
     * @param ops          项目的状态
     * @return
     */
    @ApiOperation("项目发起第4步-项目保存项目回报信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accessToken",value = "用户令牌",required = true),
            @ApiImplicitParam(name = "projectToken",value="项目令牌",required = true),
            @ApiImplicitParam(name="ops",value="用户操作类型 0-保存草稿 1-提交审核",required = true)})
    @PostMapping("/submit")
    public AppResponse<Object> submit(String accessToken,String projectToken,String ops){

        //1.前置校验，检查accessToken 有没有对象的用户信息
        String member = stringRedisTemplate.opsForValue().get(accessToken);
        if(StringUtils.isEmpty(member)){
            return AppResponse.fail("无权限，请先登录");
        }

        //2.根据项目projectToken 取出存在缓存中的项目信息
        String projectJsonStr = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);

        //3.将json格式的项目信息还原为项目对象
        ProjectRedisStorageVo storageVo = JSON.parseObject(projectJsonStr,ProjectRedisStorageVo.class);

        if(!StringUtils.isEmpty(ops)){
            //判断操作类型是1，提交审核
            if(ops.equals("1")){
                //获取项目状态提交枚举
                ProjectStatusEnume submitAuth = ProjectStatusEnume.SUBMIT_AUTH;
                //保存项目信息
                projectCreateService.saveProjectInfo(submitAuth,storageVo);

                return AppResponse.ok(null);
            }else if(ops.equals("0")){
                //获取项目 草稿状态
                ProjectStatusEnume projectStatusEnume = ProjectStatusEnume.DRAFT;
                //保存草稿
                projectCreateService.saveProjectInfo(projectStatusEnume,storageVo);
                return AppResponse.ok(null);
            }else {
                AppResponse<Object> appResponse = AppResponse.fail(null);
                appResponse.setMsg("不支持此操作");
                return appResponse;
            }
        }
        return AppResponse.fail(null);
    }

}
