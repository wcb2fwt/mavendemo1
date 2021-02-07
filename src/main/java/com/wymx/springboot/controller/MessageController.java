package com.wymx.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.wymx.springboot.entity.Message;
import com.wymx.springboot.entity.Page;
import com.wymx.springboot.entity.User;
import com.wymx.springboot.service.MessageService;
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
import org.springframework.web.util.HtmlUtils;

import javax.xml.crypto.Data;
import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        System.out.println(messageService.findConversationCount(user.getId()));
        System.out.println(page.getOffset()+"::"+ page.getLimit());
        //会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        System.out.println(conversationList);
        List<Map<String,Object>> conversations = new ArrayList<>();
        if (conversationList != null){
            for (Message message : conversationList){
                Map<String,Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        //查询系统通知未读消息数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "site/letter";
    }


    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId")String conversationId,Page page,Model model){
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        System.out.println(messageService.findLetterCount(conversationId));
        //私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        System.out.println(page.getOffset()+"::"+ page.getLimit());
        System.out.println(messageService.findLetters(conversationId, page.getOffset(), page.getLimit()));
        List<Map<String ,Object>> letters = new ArrayList<>();
        if (letterList != null){
            for (Message message : letterList){
                Map<String ,Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        //私信目标
        model.addAttribute("target", getLetterTarget(conversationId));
        //设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "site/letter-detail";
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if (letterList != null){
            for (Message message : letterList){
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }else {
            return userService.findUserById(id0);
        }
    }


    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        System.out.println(toName);
        User target = userService.findUserByName(toName);
        if (target == null){
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else {
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    //通知列表
    @RequestMapping(path = "/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();
        //查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        System.out.println("message1"+message);
        Map<String,Object> messageVo = new HashMap<>();
        if (message != null){
            messageVo.put("message", message);
            //将转义字符转回正常字符
            String content = HtmlUtils.htmlUnescape(message.getContent());
            //将正常字符串转为对象
            Map<String , Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));
            /* 查询评论主题的通知数量 */
            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("count", count);
            /* 查询评论主题的未读消息数量 */
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("unreadCount", unreadCount);
        }else {
            messageVo.put("message", null);
            messageVo.put("user", null);
            messageVo.put("entityType", null);
            messageVo.put("entityId", null);
            messageVo.put("postId", null);
            messageVo.put("count", 0);
            messageVo.put("unreadCount", 0);
        }

        model.addAttribute("commentNotice", messageVo);

        //查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        System.out.println("message2"+message);
        messageVo = new HashMap<>();
        if (message != null){
            messageVo.put("message", message);
            //将转义字符转为普通字符
            String content = HtmlUtils.htmlUnescape(message.getContent());
            //将普通字符转为对象
            Map<String , Object> data = JSONObject.parseObject(content,HashMap.class);
            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId",data.get("postId"));
            //查询点赞主题通知数量
            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count",count);
            //查询点赞主题未读通知数量
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unreadCount", unreadCount);
        }else {
            messageVo.put("message", null);
            messageVo.put("user", null);
            messageVo.put("entityType", null);
            messageVo.put("entityId", null);
            messageVo.put("postId", null);
            messageVo.put("count", 0);
            messageVo.put("unreadCount", 0);
        }
        model.addAttribute("likeNotice", messageVo);
        //查询关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        System.out.println("message3"+message);
        messageVo = new HashMap<>();
        if (message != null){
            messageVo.put("message", message);
            //将转义字符转为普通字符串
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            //查询关注主题的通知数量
            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count", count);
            //查询关注主题的未读消息数量
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unreadCount", unreadCount);
        }else {
            messageVo.put("message", null);
            messageVo.put("user", null);
            messageVo.put("entityType", null);
            messageVo.put("entityId", null);
            messageVo.put("postId", null);
            messageVo.put("count", 0);
            messageVo.put("unreadCount", 0);
        }
        model.addAttribute("followNotice", messageVo);
        //查询私信未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        //查询系统通知未读消息数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/notice";
    }

    //某个主题的通知详情列表
    @RequestMapping(path = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic")String topic,Page page,Model model){
        User user = hostHolder.getUser();
        page.setRows(messageService.findNoticeCount(user.getId(), topic));
        page.setPath("/notice/detail/"+topic);
        page.setLimit(5);

        List<Message> notices = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeVolist = new ArrayList<>();
        if (notices != null){
            for (Message notice : notices){
                Map<String,Object> map = new HashMap<>();
                //先把通知存进去
                map.put("notice", notice);
                //再存通知内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                //通知作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVolist.add(map);
            }
        }
        model.addAttribute("notices", noticeVolist);
        //设置已读
        List<Integer> ids = getLetterIds(notices);
        if (!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }
}
