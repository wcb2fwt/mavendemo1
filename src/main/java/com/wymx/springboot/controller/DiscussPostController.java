package com.wymx.springboot.controller;

import com.wymx.springboot.entity.Comment;
import com.wymx.springboot.entity.DiscussPost;
import com.wymx.springboot.entity.Page;
import com.wymx.springboot.entity.User;
import com.wymx.springboot.service.CommentService;
import com.wymx.springboot.service.DiscussPostService;
import com.wymx.springboot.service.LikeService;
import com.wymx.springboot.service.UserService;
import com.wymx.springboot.util.CommunityConstant;
import com.wymx.springboot.util.CommunityUtil;
import com.wymx.springboot.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user == null){
            return CommunityUtil.getJSONString(403, "你还没有登录！" );
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        //报错统一处理
        return CommunityUtil.getJSONString(0, "发布成功！" );
    }

    //帖子详情页面
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        //查询帖子点赞数量
        model.addAttribute("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId));
        //查询帖子点赞状态
        model.addAttribute("likeStatus", hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId));
        model.addAttribute("post", post);
        //查询帖子作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        //评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());
        //当前帖子的所有回帖
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        //评论列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if (commentList != null){
            for (Comment comment : commentList){
                //一个评论
                Map<String ,Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                //一个评论的作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //评论的点赞数量
                commentVo.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId()));
                //评论点赞的状态
                commentVo.put("likeStatus", hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId()));
                //一个评论的回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //也需要定义一个回复的列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply : replyList){
                        Map<String,Object> map = new HashMap<>();
                        //回复
                        map.put("reply", reply);
                        //作者
                        map.put("user", userService.findUserById(reply.getUserId()));
                        //回复的点赞数量
                        map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId()));
                        //回复的点赞状态
                        map.put("likeStatus",hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId()));
                        //回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        map.put("target", target);
                        replyVoList.add(map);
                    }
                }
                commentVo.put("replys", replyVoList);
                //回复的数量
                int commentCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", commentCount);
                commentVoList.add(commentVo);
            }

        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }


}
