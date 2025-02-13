package com.sky.mapper;

import com.sky.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品的id来这张中间表查询有没有出现这道菜(看菜品是否出现在套餐里)
     * @param ids
     * @return
     */
    List<Setmeal> findByDishId(List<Long> ids);
}
