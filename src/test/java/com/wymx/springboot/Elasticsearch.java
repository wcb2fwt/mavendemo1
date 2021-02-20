package com.wymx.springboot;

import com.alibaba.fastjson.JSON;
import com.wymx.springboot.dao.DiscussPostMapper;
import com.wymx.springboot.dao.elasticsearch.DiscussPostRepository;
import com.wymx.springboot.entity.DiscussPost;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.*;

@SpringBootTest
public class Elasticsearch {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    //创建索引
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

    //获取索引，判断是否存在
    @Test
    void existIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("wcb");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);

    }

    //删除索引
    @Test
    void deleteTest() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("posts");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete);
    }

    //添加文档
    @Test
    void addTest() throws IOException {
        //创建对象
        DiscussPost ds = new DiscussPost();
        ds.setCreateTime(new Date());
        ds.setTitle("biaoti");
        ds.setContent("content");
        //创建请求
        IndexRequest request = new IndexRequest("wcb");
        //设置规则
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");
        //将我们的数据放入请求  json
        request.source(JSON.toJSONString(ds), XContentType.JSON);
        //客户端发送请求
        IndexResponse index = client.index(request, RequestOptions.DEFAULT);
        System.out.println(index.toString());
        System.out.println(index.status());
    }

    //获取文档，判断是否存在
    @Test
    void testIsExists() throws IOException {
        GetRequest request = new GetRequest("wcb","1");
        boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //获取文档信息
    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("wcb","1");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
        System.out.println(response);
    }

    //更新文档信息
    @Test
    void testUpdate() throws IOException {
        UpdateRequest request = new UpdateRequest("wcb","1");
        request.timeout("1s");
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle("狂神");
        discussPost.setContent("我爱学习");
        request.doc(JSON.toJSONString(discussPost),XContentType.JSON);
        UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
        System.out.println(update.status());
        System.out.println(update);
    }

    //删除文档
    @Test
    void testDelete() throws IOException {
        DeleteRequest request = new DeleteRequest("wcb","1");
        request.timeout("1s");

        DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(delete);
        System.out.println(delete.status());
    }

    //特殊的，批量插入数据
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        //ArrayList<DiscussPost> list = new ArrayList<>();
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(103, 0, 100);
        for (int i=0; i<list.size(); i++){
            bulkRequest.add(
                    new IndexRequest("wcb")
                    .id(""+(i+1))
                    .source(JSON.toJSONString(list.get(i)),XContentType.JSON)
            );
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.status());
        System.out.println(bulk);
        System.out.println(bulk.hasFailures());
    }

    //查询
    @Test
    void testSearch() throws IOException {
        SearchRequest request = new SearchRequest("wcb");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //查询条件，我们可以使用QueryBuilders 工具来实现
        //QueryBuilders.termQuery 精确匹配
        //QueryBuilders.motchAllQuery() 匹配所有
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("id", "109");
        //MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(1));

        request.source(sourceBuilder);

        SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));
    }

    @Test
    void testSave(){
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }


}
