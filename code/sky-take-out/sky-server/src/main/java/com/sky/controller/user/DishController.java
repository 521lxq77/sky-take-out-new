package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {

        //1 查询的时候先从redis里面查询缓存数据，有的话直接返回
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get("dish_" + categoryId);

        if(list != null){
            //不用查数据库
            log.info("缓存里面有菜品数据，直接返回:{}",categoryId);
            return Result.success(list);
        }
        //2 如果没有就去查数据库
        log.info("缓存里面没有菜品数据，查询数据库:{}",categoryId);
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        list = dishService.listWithFlavor(dish);

        //3 如果是从数据库查出来的数据，那么要把数据保存到redis里面去，以便下一次查询
        redisTemplate.opsForValue().set("dish_"+categoryId,list);
        return Result.success(list);
    }

}
