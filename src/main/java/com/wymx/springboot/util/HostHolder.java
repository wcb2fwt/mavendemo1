package com.wymx.springboot.util;

import com.wymx.springboot.entity.User;
import org.springframework.stereotype.Controller;

/**
 * 持有用户信息，用于代替session对象
 */
@Controller
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
