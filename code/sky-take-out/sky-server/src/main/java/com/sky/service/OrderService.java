package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {

    /**
     * 提交订单
     * @param dto
     * @return
     */
    OrderSubmitVO add(OrdersSubmitDTO dto);
}
