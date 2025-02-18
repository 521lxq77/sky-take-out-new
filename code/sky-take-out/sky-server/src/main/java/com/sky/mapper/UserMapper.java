package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid = #{openId}")
    User findByOpenId(String openId);

    @Options(keyProperty = "id", useGeneratedKeys = true)
    @Insert("insert into user (openid,create_time) values (#{openid},#{createTime})")
    void add(User user);

    @Select("select * from user where id = #{id}")
    User findById(Long id);

    /**
     * 根据动态条件统计用户数量
     *
     * @param map
     * @return
     */
    Integer countByMap(Map<String, Object> map);

}
