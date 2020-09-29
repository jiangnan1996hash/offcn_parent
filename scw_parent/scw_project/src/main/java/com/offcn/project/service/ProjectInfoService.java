package com.offcn.project.service;

import com.offcn.project.pojo.*;

import java.util.List;

/**
 * 项目的回报列表
 *
 */
public interface ProjectInfoService {

    /**
     * 获取项目回报列表
     * @param projectId
     * @return
     */
    public List<TReturn> getProjectReturns(Integer projectId);



    /**
     * 获取系统中所有项目
     * @return
     */
    public List<TProject> getAllProjects();

    /**
     * 获取项目图片
     * @param id
     * @return
     */
    public List<TProjectImages> getProjectImages(Integer id);


    /**
     * 获取项目的详细信息
     * @param projectId
     * @return
     */
    public TProject getProjectInfo(Integer projectId);


    /**
     *  获得项目标签
     * @return
     */
    public List<TTag> getAllProjectTags();


    /**
     * 获取项目分类
     * @return
     */
    List<TType> getProjectTypes();

    /**
     * 获取回报信息
     * @param returnId
     * @return
     */
    TReturn getReturnInfo(Integer returnId);



}
