package com.wymx.springboot.service;

import com.wymx.springboot.dao.LoginTicketMapper;
import com.wymx.springboot.dao.UserMapper;
import com.wymx.springboot.entity.LoginTicket;
import com.wymx.springboot.entity.User;
import com.wymx.springboot.util.CommunityConstant;
import com.wymx.springboot.util.CommunityUtil;
import com.wymx.springboot.util.MailClient;
import com.wymx.springboot.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;
    //private LoginTicketMapper loginTicketMapper;

    /**
     *发邮件时要生成一个激活码，激活码中要包含域名，还得包含我们的项目名
     */
    //将配置文件中的域名注入进来
    @Value("${community.path.domain}")
    private String domain;
    //将配置文件中的项目名注入进来
    @Value("${server.servlet.context-path}")
    private String contextPath;



    //查询用户
    public User findUserById(int id){
        //return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null){
            user = initUser(id);
        }
        return user;
    }


    //用户注册
    public Map<String , Object> register(User user){
        HashMap<String , Object> map = new HashMap<>();
        //空值处理
        if (user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空！");
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u!=null){
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        //验证邮箱
        if (u!=null){
            map.put("emailMsg", "该邮箱已被使用！");
            return map;
        }
        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));//获取随机5为字符串
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //邮件格式
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    //用户激活
    public int activation(int userId , String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

    //用户登录
    public Map<String ,Object> login(String username,String password,int expiredSeconds){
        Map<String ,Object> map = new HashMap<>();
        //空值判断
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空！");
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空！");
        }
        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        if (user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password+user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码不正确！");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    //退出登录
    public void logout(String ticket){
        //loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    //根据凭证查询登录信息
    public LoginTicket findByTicket(String ticket){
        //return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    //更新用户头像
    public int updateHeader(int userId,String headerUrl){
        //return userMapper.updateHeader(userId, headerUrl);
        int i = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return i;
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

    /**
     * 1、优先从缓存中取值
     * 2、取不到时初始化缓存数据
     * 3、数据发生变更时清除缓存数据
     */
    //1、优先从缓存中取值
    private User getCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }
    //2.取不到时初始化缓存数据
    private User initUser(int userId){
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user,3600, TimeUnit.SECONDS);
        return user;
    }
    //3.数据变更时清除缓存
    private void clearCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
}
