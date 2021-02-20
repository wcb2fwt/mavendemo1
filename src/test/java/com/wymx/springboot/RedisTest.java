package com.wymx.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;


@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public void testStrings(){
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 10);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }


    @Test
    public void testHash(){
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "name", "zhang");
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "name"));
        System.out.println(redisTemplate.opsForHash().increment(redisKey, "id", 1));
    }

    @Test
    public void testList(){
        String redisKey = "test:list";
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 1));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 4));
        redisTemplate.opsForList().rightPop(redisKey);
        System.out.println(redisTemplate.opsForList().size(redisKey));
    }

    @Test
    public void testSet(){
        String redisKey = "test:set";

        redisTemplate.opsForSet().add(redisKey, "wcb","rj","wjk","yk","gh","rj","rj");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
        redisTemplate.opsForSet().remove(redisKey, "yk");
        System.out.println(redisTemplate.opsForSet().size(redisKey));

    }

    //绑定key
    @Test
    public void testBoundOperations(){
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        System.out.println(operations.get());
    }

    //编程式事务
    @Test
    public void testTransactional(){
        redisTemplate.multi();
        redisTemplate.exec();
    }
}
