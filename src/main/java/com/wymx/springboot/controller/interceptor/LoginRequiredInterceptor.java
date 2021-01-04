package com.wymx.springboot.controller.interceptor;

import com.wymx.springboot.annotation.LoginRequired;
import com.wymx.springboot.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断handler是不是一个方法
        if (handler instanceof HandlerMethod){
            //如果是的话，可以对他进行转型
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //直接获取它拦截到的method对象
            Method method = handlerMethod.getMethod();
            //获取我们需要拦截的有这个注解的方法
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //判断是否有这个注解方法的请求，
            if (loginRequired != null && hostHolder.getUser() == null){
                //若有这个方法的请求且当前用户没有登录
                //重定向到登录页面
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
