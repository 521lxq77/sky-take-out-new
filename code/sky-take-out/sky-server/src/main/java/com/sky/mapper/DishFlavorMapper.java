package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

//    void add(DishFlavor dishFlavor);

    /**
     * 批量添加口味数据
     * @param flavorList
     */
    void add(List<DishFlavor> flavorList);

    /**
     * 根据菜品口味的ID批量删除口味信息
     * @param ids
     */
    void deleteByDishId(List<Long> ids);

    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> findByDishId(Long id);
}
