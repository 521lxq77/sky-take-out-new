package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * 1 执行数据的转化、把dto转化为实体类
     * 2 查询数据库，判定这道菜or套餐，到底是新的还是旧的数据
     * 3 如果查询到是新的购物车数据，那么就执行数据库的添加操作
     *  3.1 根据给定的菜品or套餐id 查询出具体的数据
     *  3.2 给实体类封装 name image amount create_time number=1 user_id
     * 4 如果购物车有这条数据，进行更新操作
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //1 转化数据
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //2 查询是新、旧数据 （user_id(1)  dish_id(0) setmeal_id(0) dishFlavor(0)）
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if(list != null && list.size() > 0){
            //3 获取集合里面0号下标的元素
            ShoppingCart cartINDB = list.get(0);
            cartINDB.setNumber(cartINDB.getNumber() + 1);
            shoppingCartMapper.update(cartINDB);
        }
        else{
            //5 为数据库新增数据(需要先查询是套餐还是菜品数据)
            // 是菜品
            if(shoppingCart.getDishId() != null){
                Dish dish = dishMapper.findDishById(shoppingCartDTO.getDishId());
                // 补充数据
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCart.setNumber(1);

                shoppingCartMapper.add(shoppingCart);

            }else{
                // 是套餐
                Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
                // 补充数据
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCart.setNumber(1);

                shoppingCartMapper.add(shoppingCart);

            }
        }


    }

    /**
     * 查询购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        shoppingCartMapper.deleteByUid(BaseContext.getCurrentId());
    }

}
