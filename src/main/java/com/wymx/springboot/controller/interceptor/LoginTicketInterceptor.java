package com.wymx.springboot.controller.interceptor;

import com.wymx.springboot.entity.LoginTicket;
import com.wymx.springboot.entity.User;
import com.wymx.springboot.service.UserService;
import com.wymx.springboot.util.CookieUtil;
import com.wymx.springboot.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    //在Controller之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findByTicket(ticket);
            //检查凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    //在模版之前调用
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null){
            //将用户信息添加到模版
            modelAndView.addObject("loginUser",user);
        }
    }

    //在整个请求执行完成之后执行的方法
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        hostHolder.clear();
    }
}
