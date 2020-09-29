package com.offcn.project.controller;

import com.offcn.project.pojo.*;
import com.offcn.project.service.ProjectInfoService;
import com.offcn.project.vo.resp.ProjectDetailVo;
import com.offcn.project.vo.resp.ProjectVo;
import com.offcn.response.AppResponse;
import com.offcn.util.OssTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project")
@Api(tags = "项目基本功能模块（文件上传、项目信息获取等）")
@Slf4j
public class ProjectInfoController {

    @Autowired
    private ProjectInfoService projectInfoService;

    @Autowired
    private OssTemplate ossTemplate;

    @ApiOperation("文件上传功能")
    @PostMapping("/upload")                                             //文件上传
    public AppResponse<Map<String,Object>> upload(@RequestParam("file") MultipartFile[] files)throws IOException {
        Map<String,Object> map = new HashMap<>();
        List<String> list= new ArrayList<>();
        if(files!=null&&files.length>0){
            for(MultipartFile item:files){
                if(!item.isEmpty()){
                    String upload = ossTemplate.upload(item.getInputStream(),item.getOriginalFilename());
                    list.add(upload);
                }
            }
        }
        map.put("urls",list);
        log.debug("ossTemplate信息：{},文件上传成功访问路径{}",ossTemplate,list);
        return AppResponse.ok(map);
    }

    @ApiOperation("根据项目ID获取项目的回报列表")
    @GetMapping("//details/returns/{projectId}")
    public AppResponse<List<TReturn>> detailsReturn(@PathVariable("projectId") Integer projectId) {

        List<TReturn> returns = projectInfoService.getProjectReturns(projectId);
        return AppResponse.ok(returns);
    }



    @ApiOperation("获取系统所有的项目")
    @GetMapping("/all")
    public AppResponse<List<ProjectVo>> all() {
        // 1、创建集合存储全部项目的VO
        List<ProjectVo> prosVo = new ArrayList<>();
        // 2、查询全部项目
        List<TProject> pros = projectInfoService.getAllProjects();
        //3、遍历项目集合
        for (TProject tProject : pros) {
            //获取项目编号
            Integer id = tProject.getId();
            //根据项目编号获取项目配图
            List<TProjectImages> images = projectInfoService.getProjectImages(id);
            ProjectVo projectVo = new ProjectVo();
            BeanUtils.copyProperties(tProject, projectVo);
            //遍历项目配图集合
            for (TProjectImages tProjectImages : images) {
                //如果图片类型是头部图片，则设置头部图片路径到项目VO
                if (tProjectImages.getImgtype() == 0) {
                    projectVo.setHeaderImage(tProjectImages.getImgurl());
                }
            }
            //把项目vo添加到项目vo集合
            prosVo.add(projectVo);
        }
        return AppResponse.ok(prosVo);
    }


    @ApiOperation("获取项目信息详情")
    @GetMapping("/details/info/{projectId}")
    public AppResponse<ProjectDetailVo> detailsInfo(@PathVariable("projectId") Integer projectId){


        TProject projectInfo = projectInfoService.getProjectInfo(projectId);
        ProjectDetailVo projectVo = new ProjectDetailVo();

        //查出这个项目的所有图片
        List<TProjectImages> projectImages  = projectInfoService.getProjectImages(projectInfo.getId());
        //查询图片信息
        List<String> detailsImage = projectVo.getDetailsImage();

        //判断详情图片是否为空
        if(detailsImage==null){
            detailsImage=new ArrayList<String>();
        }

        for (TProjectImages tProjectImages : projectImages) {
            if (tProjectImages.getImgtype() == 0) {//头图片
                projectVo.setHeaderImage(tProjectImages.getImgurl());
            } else {
                detailsImage.add(tProjectImages.getImgurl());
            }
        }

        projectVo.setDetailsImage(detailsImage);

        // 2、项目的所有支持回报；
        List<TReturn> returns = projectInfoService.getProjectReturns(projectInfo.getId());
        projectVo.setProjectReturns(returns);
        //将基本数据转存到响应中去
        BeanUtils.copyProperties(projectInfo, projectVo);
        return AppResponse.ok(projectVo);

    }


    @ApiOperation("获取系统的所有标签")
    @GetMapping("/tags")
    public AppResponse<List<TTag>> tags(){
        List<TTag> tages = projectInfoService.getAllProjectTags();
        return AppResponse.ok(tages);

    }


    @ApiOperation("获取系统所有的项目分类")
    @GetMapping("/types")
    public AppResponse<List<TType>> types(){
        List<TType> projectTypes = projectInfoService.getProjectTypes();
        return AppResponse.ok(projectTypes);
    }


    @ApiOperation("获取回报信息")
    @GetMapping("/returns/info/{returnId}")
    public AppResponse<TReturn> getTReturn(@PathVariable("returnId") Integer returnId){
        TReturn returnInfo = projectInfoService.getReturnInfo(returnId);
        return AppResponse.ok(returnInfo);
    }






}
