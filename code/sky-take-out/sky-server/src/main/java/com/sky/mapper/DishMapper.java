package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * 新增一条记录后,可以让mybatis把这条记录的ID值返回回来,但是不会再用方法返回值返回,而是告诉他
     * 执行成功后,把ID值放到参数里面的某个属性身上去,使用数据库自增的ID值
     * @param dish
     */
    @Options(keyProperty = "id", useGeneratedKeys = true)
//    @AutoFill
//    @Insert("insert into dish values (null, #{name}, #{categoryId}, #{price}, #{image}, #{description}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(OperationType.INSERT)
    @Insert("INSERT INTO dish (name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) " +
            "VALUES (#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void add(Dish dish);

    /**
     * 菜品分类
     * @param dto
     * @return
     */
    Page<DishVO> page(DishPageQueryDTO dto);

    /**
     * 根据菜品的ID,查询菜品数据,必须约定只查询起售状态的菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish findDishById(Long id);


    /**
     * 根据菜品的ID批量删除菜品
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 更新菜品操作
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    @Update("update dish set name = #{name}, category_id = #{categoryId}, " +
            "price = #{price}, image = #{image}, description = #{description}, " +
            "status = #{status}, update_user = #{updateUser}, " +
            "update_time = #{updateTime} where id = #{id}")
    void update(Dish dish);
}
