package com.wymx.springboot;

import com.wymx.springboot.dao.DiscussPostMapper;
import com.wymx.springboot.dao.UserMapper;
import com.wymx.springboot.entity.DiscussPost;
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

}
