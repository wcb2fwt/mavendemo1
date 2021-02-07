package com.wymx.springboot.config;

import com.wymx.springboot.controller.interceptor.AlphaInterceptor;
import com.wymx.springboot.controller.interceptor.LoginRequiredInterceptor;
import com.wymx.springboot.controller.interceptor.LoginTicketInterceptor;
import com.wymx.springboot.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*拦截器配置类*/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaInterceptor alphaInterceptor;
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;
    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;
    @Autowired
    private MessageInterceptor messageInterceptor;


    public void addInterceptors(InterceptorRegistry registration){
        registration.addInterceptor(alphaInterceptor)
                .excludePathPatterns("**/*.css","**/*.js","**/*.png","**/*.jpg","**/*.jpeg")
                .addPathPatterns("/register","/login");

        registration.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("**/*.css","**/*.js","**/*.png","**/*.jpg","**/*.jpeg");

        registration.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("**/*.css","**/*.js","**/*.png","**/*.jpg","**/*.jpeg");
        /*对所有对静态资源不拦截，对所有的动态资源都要拦截*/
        registration.addInterceptor(messageInterceptor)
                .excludePathPatterns("**/*.css","**/*.js","**/*.png","**/*.jpg","**/*.jpeg");
    }
}
