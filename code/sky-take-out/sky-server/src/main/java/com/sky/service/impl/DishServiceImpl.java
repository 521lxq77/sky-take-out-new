package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     * 新增菜品
     * 1 菜品数据包含菜品的基本数据 和 口味数据
     * 2 菜品数据是保存在菜品表(dish),口味数据是保存在口味表(dish_flavor)
     * 3 所以新增一道菜,就需要往两张表里面添加数据
     *   需要注入进来dish表 对应的 DishMapper
     *   需要注入进来 dish_flavor 对应 DishFlavourMapper
     *   先调用DishMapper 后DishFlavourMapper
     * @param dishDTO
     */
    @Override
    public void add(DishDTO dishDTO) {
        //1 dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.add(dish);

        //2 flavor
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){

            flavors.forEach(dishFlavor -> {
                // 需要添加菜品的时候,让mybatis返回菜品的主键ID
                dishFlavor.setDishId(dish.getId());
            });
            dishFlavorMapper.add(flavors);
        }

    }

    /**
     * 菜品分页
     *
     * @param dto
     * @return PageResult
     */
    @Override
    public PageResult page(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        Page<DishVO> page = dishMapper.page(dto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     * 1 可以删除一个次啊品,也可以删除多个菜品
     * 2 起售中的菜品不能删除
     * 3 如果有套餐包含这道菜,那么也不能删除
     * 4 除此之外,删除菜品后,相应的口味也要删除
     * @param ids
     */
    @Override
    public void delete(List<Long> ids) {
        //1 先看有没有属于起售的菜
        ids.forEach(id->{
            Dish dish = dishMapper.findDishById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });

        //2 再看有没有属于套餐的菜品
        List<Setmeal> setmealDishList = setmealDishMapper.findByDishId(ids);
        if(setmealDishList != null && setmealDishList.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
        }


        //3 以上均没有就可以进行删除操作了
        // 删除菜品
        dishMapper.delete(ids);
        // 删除口味
        dishFlavorMapper.deleteByDishId(ids);

    }

    /**
     * 根据菜品ID查询菜品
     * 菜品数据既包含dish 也包含 flavor;所以这里查询两张表打包成vo向上返回
     * @param id
     * @return
     */
    @Override
    public DishVO findByID(Long id) {
        //1 根据菜品id查询菜品
        Dish dish = dishMapper.findDishById(id);
        //2 根据菜品id查询菜品口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.findByDishId(id);
        //3 打包
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 更新菜品
     * 可能要更新两张表
     * 1 先往菜品表更新数据
     * 2 更新口味表(可以视为 先 删除 后 添加操作)
     * @param dishDTO
     */
    @Override
    public void update(DishDTO dishDTO) {
        //1 更新菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        //2 更新口味(先删后加)
        // 包装成list后删除
        dishFlavorMapper.deleteByDishId(Arrays.asList(dishDTO.getId()));
        if(dishDTO.getFlavors() !=null && dishDTO.getFlavors().size() > 0){
            //获取口味数据
            List<DishFlavor> flavorList = dishDTO.getFlavors();
            //设置每一个口味属于哪一道菜
            flavorList.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            // 批量添加
            dishFlavorMapper.add(dishDTO.getFlavors());
        }

    }
}
