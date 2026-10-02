package com.hla.gysc.service.impl;

import com.hla.gysc.dao.UserMapper;
import com.hla.gysc.entity.User;
import com.hla.gysc.service.UserService;
import com.hla.gysc.util.OrderUtil;
import com.hla.gysc.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @Override
    public boolean add(User user) {
        log.info("执行用户插入操作: {}", user);
        try {
            Integer result = userMapper.insertOne(user);
            log.info("用户插入结果: {}", result);
            return result > 0;
        } catch (Exception e) {
            log.error("用户插入异常", e);
            throw e;
        }
    }

    @Override
    public boolean update(User user) {
        return userMapper.updateOne(user) > 0;
    }

    @Override
    public List<User> getList(User user, OrderUtil orderUtil, PageUtil pageUtil) {
        return userMapper.selectUserList(user, orderUtil, pageUtil);
    }

    @Override
    public User get(Integer user_id) {
        return userMapper.selectOne(user_id);
    }

    @Override
    public User login(String user_name, String user_password) {
        return userMapper.selectByLogin(user_name, user_password);
    }

    @Override
    public Integer getTotal(User user) {
        return userMapper.selectTotal(user);
    }
}
