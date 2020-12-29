package com.wymx.springboot.service;

import com.wymx.springboot.dao.UserMapper;
import com.wymx.springboot.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    //lll
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

}
