package com.offcn.order.service;


import com.offcn.order.service.impl.ProjectServiceFeignException;
import com.offcn.order.vo.req.TReturn;
import com.offcn.response.AppResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * feign的声明式事务调用
 */
//指定feign的客户端
@FeignClient(value = "SCW-PROJECT",fallback = ProjectServiceFeignException.class)
public interface ProjectServiceFeign {


    /**
     * fegin的声明式调用
     * @param projectId
     * @return
     */
    @GetMapping("/project/details/returns/{projectId}")
    public AppResponse<List<TReturn>> returnInfo(@PathVariable(name="projectId") Integer projectId);



}
