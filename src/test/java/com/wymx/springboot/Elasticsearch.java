package com.wymx.springboot;

import com.wymx.springboot.dao.DiscussPostMapper;
import com.wymx.springboot.dao.elasticsearch.DiscussPostRepository;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class Elasticsearch {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Test
    public void indexTest() throws IOException {
//        Map<String,Object> jsonMap = new HashMap<>();
//        jsonMap.put("user", "wcb");
//        jsonMap.put("postDate", new Date());
//        jsonMap.put("message", "wo ai xue bian cheng");
        CreateIndexRequest request = new CreateIndexRequest("wcb");
                //.id("1")
               // .source(jsonMap);
        request.timeout();
        CreateIndexResponse index = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(index);
    }

    @Test
    void existIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("wcb");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);

    }


}
