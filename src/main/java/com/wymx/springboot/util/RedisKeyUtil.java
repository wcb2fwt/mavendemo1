package com.wymx.springboot.util;

public class RedisKeyUtil {

    //redis - key  分隔符
    private static final String SPLIT = ":";
    //帖子前缀
    private static final String P_E_L = "like:entity";
    //用户前缀
    private static final String P_U_L = "like:user";
    //粉丝前缀
    private static final String P_FOLLOWEE = "followee";
    //被关注者前缀
    private static final String P_FOLLOWER = "follower";
    //验证码前缀
    private static final String P_KAPTCHA = "kaptcha";
    //登录凭证
    private static final String P_TICKET = "ticket";
    //用户前缀
    private static final String P_USER = "user";

    //某个实体的赞
    //like:entity:entityType:entityId --> 用集合存储
    public static String getEntityLikeKey(int entityType,int entityId){
        return P_E_L+SPLIT+entityType+SPLIT+entityId;
    }

    //某一个用户的赞
    //like:user:userId
    public static String getUserLikeKey(int userId){
        return P_U_L + SPLIT + userId;
    }

    //某个用户关注的实体
    //followee:userId:entityType --> zset(entityId,now)
    public static String getFolloweeKey(int userId,int entityType){
        return P_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    //某个实体拥有的粉丝
    //follower:entityType:entityId --> zset(userId,now)
    public static String getFollowerKey(int entityType,int entityId){
        return P_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //登录验证码
    public static String getKaptcha(String owner){
        return P_KAPTCHA + SPLIT + owner;
    }

    //用户登录凭证
    public static String getTicketKey(String ticket){
        return P_TICKET + SPLIT + ticket;
    }

    //用户key
    public static String getUserKey(int userId){
        return P_USER + SPLIT + userId;
    }
}
