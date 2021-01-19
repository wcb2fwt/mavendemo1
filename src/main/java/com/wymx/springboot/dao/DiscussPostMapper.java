package com.wymx.springboot.dao;

import com.wymx.springboot.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    //param注解用于给参数取别名
    //如果只有一个参数，并且在<if>里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    //添加帖子的方法
    int insertDiscussPost(DiscussPost discussPost);

    //根据id查询帖子
    DiscussPost selectDiscussPostById(int id);

    //更新帖子评论数量
    int updateCommentCount(int id,int commentCount);




}
