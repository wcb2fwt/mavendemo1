package com.wymx.springboot.controller;

import com.wymx.springboot.entity.Comment;
import com.wymx.springboot.entity.DiscussPost;
import com.wymx.springboot.entity.Event;
import com.wymx.springboot.event.EventProducer;
import com.wymx.springboot.service.CommentService;
import com.wymx.springboot.service.DiscussPostService;
import com.wymx.springboot.util.CommunityConstant;
import com.wymx.springboot.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;

    //添加帖子评论
    @RequestMapping(path = "/add/{discussPostId}" , method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        //触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            //如果是给帖子评论，去帖子表中查到这个帖子
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            //帖子中用户id
            event.setEntityUserId(target.getUserId());
        }
        if (comment.getEntityType() == ENTITY_TYPE_COMMENT){
            //如果类型为评论，查找到这个评论
            Comment target = commentService.findCommentById(comment.getEntityId());
            //设置评论表中的用户id
            event.setEntityUserId(target.getUserId());
        }
        //发布系统消息
        eventProducer.fireEvent(event);
        return "redirect:/discuss/detail/"+discussPostId;
    }
}
