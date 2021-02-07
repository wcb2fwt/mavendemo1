package com.wymx.springboot.controller;

import com.wymx.springboot.entity.DiscussPost;
import com.wymx.springboot.entity.Page;
import com.wymx.springboot.entity.User;
import com.wymx.springboot.service.DiscussPostService;
import com.wymx.springboot.service.LikeService;
import com.wymx.springboot.service.MessageService;
import com.wymx.springboot.service.UserService;
import com.wymx.springboot.util.CommunityConstant;
import com.wymx.springboot.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;

    //帖子详情
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用之前，springMVC会自动实例化Model和Page，并将Page注入Model
        //所以，我们在thymeleaf中可以直接访问Page对象中的数据。
        page.setRows(discussPostService.findDiscussPostRows(0));
        System.out.println(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        System.out.println(page.getOffset()+"::"+page.getLimit());
        System.out.println(list);
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list != null){
            for (DiscussPost post:
                 list) {
                Map<String,Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                //查询赞的数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }

}
