package com.wymx.springboot;

import com.sun.tools.javac.Main;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@SpringBootTest
public class KafkaTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka(){
        kafkaProducer.sendMessage("test", "ni zai gan ma?");
        kafkaProducer.sendMessage("test", "你好");
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
@Component
class KafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;

    //生产者发消息
    public void sendMessage(String topic,String content){
        kafkaTemplate.send(topic,content);
    }
}
@Component
class KafkaConsumer{

    //消费者读消息
    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord recorde){
        System.out.println(recorde.value());
    }
}
