package com.wymx.springboot.dao;

import com.wymx.springboot.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    //查询回帖
    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);
    //查询回复数量
    int selectCountByEntity(int entityType,int entityId);
    //增加评论
    int insertComment(Comment comment);
}
