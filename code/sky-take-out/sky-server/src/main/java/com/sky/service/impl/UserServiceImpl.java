package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    String url = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 用户登录
     * 1. 使用httpclient发起请求，把code+appid+secret 一并发给微信的官方
     * 2. 接收结果：
     *      2.1 判定返回的openid（String）是否有值
     *      2.2 如果openid没有值，表示登录失败
     *          抛出一个登录失败的异常
     *      2.3 如果openid有值，表示登陆成功
     *          2.3.1 根据openid查询 user表数据 查到表示是老用户，直接返回user
     *          2.3.2 没有查到，表示是新用户，将用户的信息添加到user表中，获取主键返回
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        // 1. 使用httpclient发起请求，把code+appid+secret 一并发给微信的官方
        Map<String,String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",userLoginDTO.getCode());
        map.put("grant_type","authorization_code");
        // 获取微信官方返回的结果
        String result = HttpClientUtil.doGet(url, map);

        //2. 判定返回的openid（String）是否有值
        JSONObject jsonObject = JSON.parseObject(result);
        String openid = jsonObject.getString("openid");
        // 失败
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 成功
        User user = userMapper.findByOpenId(openid);

        if(user == null){
            // 新用户
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userMapper.add(user);
        }

        // 组装UserLoginVO
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO vo = new UserLoginVO(user.getId(), openid, token);

        return vo;
    }
}
