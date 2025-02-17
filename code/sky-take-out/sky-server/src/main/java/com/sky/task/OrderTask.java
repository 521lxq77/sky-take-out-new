package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理支付超时的订单
     */
    // @Scheduled(cron = "0 * * * * ?")
    // 从当前时间算起，每隔一分钟就执行
    @Scheduled(fixedDelay = 1000*60)
    public void processPayTimeOut(){
        log.info("处理支付超时订单：{ }", new Date());

        // 查询状态（未支付） 超时 订单
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);

        List<Orders> orderList = orderMapper.getByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT, time);

        if(orderList != null && orderList.size() >0){
            orderList.forEach(order->{
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("支付超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            });
        }
    }


    /**
     * 处理派送中的订单
     * 每天凌晨1点时候执行：处理配送超时的订单【检查】
     */
//    @Scheduled(cron = "0 0 0 * * ? *")
    @Scheduled(cron = "0 * * * * ?")
    public void processDeliveryOrder(){
        log.info("处理派送超时的订单");

        //1 找订单 1小时前的订单，派送中的（4）
        LocalDateTime time = LocalDateTime.now().minusMinutes(60);
        List<Orders> orderList = orderMapper.getByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        //2 修改订单
        if(orderList != null && orderList.size() > 0){
            orderList.forEach(orders ->{
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
                orderMapper.update(orders);
            });
        }

    }

}
