package com.wymx.springboot.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    //生成一个随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //MD5加密（密码+随机字符串）
    public static String md5(String key){
        if (StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }


    /**
     * 平时服务器给浏览器返回的json数据往往需要包含几部分内容，我们需要将这几部分整合到一起。
     * 1、编码
     * 2、提示信息
     * 3、业务数据
     */
    public static String getJSONString(int code, String msg, Map<String ,Object> map){
        //调用者将这3个数据给我，我把它封装成一个JSON对象，将JSON对象转换成字符串，我们就得到一个JSON格式的字符串
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null){
            for (String key : map.keySet()
                 ) {
                json.put(key, map.get(key));
            }
        }
        //将JSON对象转换成字符串
        return json.toJSONString();
    }
    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }
    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }

}
