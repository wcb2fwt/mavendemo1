package com.wymx.springboot;

import com.wymx.springboot.dao.DiscussPostMapper;
import com.wymx.springboot.dao.LoginTicketMapper;
import com.wymx.springboot.dao.MessageMapper;
import com.wymx.springboot.dao.UserMapper;
import com.wymx.springboot.entity.DiscussPost;
import com.wymx.springboot.entity.LoginTicket;
import com.wymx.springboot.entity.Message;
import com.wymx.springboot.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class MapperTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void messageTest(){
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list){
            System.out.println(message);
        }
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);
        List<Message> messages = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : messages){
            System.out.println(message);
        }
        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

    }

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);
        User user1 = userMapper.selectByName("liubei");
        System.out.println(user1);
        User user2 = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user2);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("王淳博");
        user.setPassword("asdqwerty");
        user.setSalt("abc");
        user.setEmail("sda2@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/102.png");
        user.setCreateTime(new Date());
        int i = userMapper.insertUser(user);
        System.out.println(i);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser(){
        int i = userMapper.updateStatus(150, 2);
        System.out.println(i);
        int i1 = userMapper.updatePassword(150, "123123");
        System.out.println(i1);
        int i2 = userMapper.updateHeader(150, "wcb.png");
        System.out.println(i2);
    }

    @Test
    public void testSelectPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost post : list){
            System.out.println(post);
        }
        int i = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(i);
    }

    @Test
    public void loginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(1);
        loginTicket.setTicket("wert");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void selectLoginTicket(){
        get();
    }

    @Test
    public void get(){
        boolean index = false;
        if (index) System.out.println("true");;
        if (!index) System.out.println("2");
    }

    public void get1(){
        boolean index = false;
        if (index){
            System.out.println("true");;
        }else {
            System.out.println("2");
        }

    }

}
