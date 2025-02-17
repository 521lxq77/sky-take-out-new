package com.sky.controller.user;


import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "C端-订单接口")
@RequestMapping("/user/order")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;
    /**
     * 提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @ApiOperation("提交订单")
    @PostMapping("/submit")
    public Result add(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO vo = orderService.add(ordersSubmitDTO);
        return Result.success(vo);
    }


    @ApiOperation("订单支付")
    @PutMapping("/payment")
    public Result payment(OrdersPaymentDTO ordersPaymentDTO){
        return Result.success();
    }

    @RequestMapping("/paySuccess")
    public Result paySuccess(){
        // 去修改订单  修改状态  修改原因  开始制作菜品 联系骑手 开始送外卖
        return Result.success();
    }


}
