package com.offcn.webui.controller;


import com.offcn.response.AppResponse;
import com.offcn.webui.service.OrderServiceFeign;
import com.offcn.webui.vo.resp.OrderFormInfoSubmitVo;
import com.offcn.webui.vo.resp.ReturnPayConfirmVo;
import com.offcn.webui.vo.resp.TOrder;
import com.offcn.webui.vo.resp.UserRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
public class OrderController {

    @Autowired
    private OrderServiceFeign orderServiceFeign;

    @RequestMapping("/order/save")
    public String OrderPay(OrderFormInfoSubmitVo vo, HttpSession session){

        UserRespVo userResponseVo = (UserRespVo) session.getAttribute("sessionMember");

        if (userResponseVo == null){
            return "redirect:/login.html";
        }

        //获取用户的令牌
        String accessToken = userResponseVo.getAccessToken();
        vo.setAccessToken(accessToken);

        //从session中获取回报列表的数据
        ReturnPayConfirmVo confirmVo = (ReturnPayConfirmVo) session.getAttribute("returnConfirmSession");

        if (confirmVo == null){
            return "redirect:/login.html";
        }

        vo.setProjectid(confirmVo.getProjectId());
        vo.setReturnid(confirmVo.getId());
        vo.setRtncount(confirmVo.getNum());

        //创建订单
        AppResponse<TOrder> order = orderServiceFeign.createOrder(vo);
        TOrder data = order.getData();

        //下单成功，打印相关信息待处理
        String orderName = confirmVo.getProjectName();
        System.out.println("orderNum:"+data.getOrdernum());
        System.out.println("money:"+data.getMoney());
        System.out.println("orderName:"+orderName);
        System.out.println("Remark:"+vo.getRemark());

        return "/member/minecrowdfunding";
    }



}
