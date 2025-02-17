package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 提交订单
     * 1. 订单数据包含 基本数据和订单的详情数据【买了什么东西】
     * 2. 他们存储在不同表里面
     *      订单基本数据在 订单表 orders
     *      订单详情 在订单详情表 order_detail
     * 3. 需要向两张表中添加数据
     *      先添加订单表 获取到id
     *      再添加订单详情表
     * @param dto
     * @return
     */
    @Override
    public OrderSubmitVO add(OrdersSubmitDTO dto) {
        //1 向订单表添加
        //1.0 转化数据模型
        Orders orders = new Orders();
        BeanUtils.copyProperties(dto,orders);

        //1.1.1 准备用户数据
        User user = userMapper.findById(BaseContext.getCurrentId());
        //1.1.2 准备地址信息
        AddressBook addressBook = addressBookMapper.getById(dto.getAddressBookId());

        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //1.2 补充数据
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setNumber(UUID.randomUUID().toString().replace("-",""));
        orders.setUserId(BaseContext.getCurrentId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setUserName(user.getName());
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());

        String address = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        orders.setAddress(address);

        //1.3 添加到订单表里
        orderMapper.add(orders);


        //2 添加订单详情表
        //2.1 先获取当前用户的购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());

        //2.2 购物车数据转订单详情数据后批量添加
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(new Function<ShoppingCart, OrderDetail>() {
            /**
             * Applies this function to the given argument.
             *
             * @param shoppingCart the function argument
             * @return the function result
             */
            @Override
            public OrderDetail apply(ShoppingCart shoppingCart) {
                // 2.2.1 遍历一次 构建一个新的OrderDetail对象
                OrderDetail od = new OrderDetail();

                // 2.2.2 搬运数据
                BeanUtils.copyProperties(shoppingCart, od);

                // 2.2.3 设置订单详情属于哪一个订单
                od.setOrderId(orders.getId());
                return od;
            }
        }).collect(Collectors.toList());

        //2.3 向订单详情表中添加数据
        orderDetailMapper.addAll(orderDetailList);


        //3 清空购物车
        shoppingCartMapper.deleteByUid(BaseContext.getCurrentId());

        //4 组件返回的结果
        return OrderSubmitVO.builder().
                id(orders.getId()).
                orderNumber(orders.getNumber()).
                orderAmount(orders.getAmount()).
                orderTime(orders.getOrderTime())
                .build();

    }
}
