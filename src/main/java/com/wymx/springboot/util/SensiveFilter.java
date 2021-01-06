package com.wymx.springboot.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensiveFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensiveFilter.class);
    private static final String REPLACE = "***";
    //定义根节点
    private TrieNode rootNode = new TrieNode();

    //定义初始化方法
    @PostConstruct
    private void init(){
        //加载敏感词文件(加载到的是一个字节流)
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive words.txt");
                //将字节流转换为缓冲流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            String keyword;
            //读一行，看是否为空
            while ((keyword = reader.readLine()) != null){
               this.addKeywoed(keyword);
            }

        } catch (Exception e) {
            LOGGER.error("加载敏感词文件失败："+e.getMessage());
        }
    }
    //过滤敏感词方法
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        //如果传入的字符串不为空，我们需要定义3个指针来过滤
        //指针1
        TrieNode trieNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuffer sb = new StringBuffer();
        while (position < text.length()){
            //得到当前某个字符
            char c = text.charAt(position);

            //跳过特殊字符
            if (isSymbol(c)){
                //若指针1处于根节点,将此符号计入结果，让指针2向下走一步
                if (trieNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            //检查下级节点
            trieNode = trieNode.getSubNode(c);
            if (trieNode == null){
                //以begin开头的字符不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                //重新指向根节点
                trieNode = rootNode;
            }else if (trieNode.isKeywordEnd()){
                //找到敏感词，将begin～position这段替换掉
                sb.append(REPLACE);
                //进入下一个位置
                begin = ++position;
                trieNode = rootNode;
            }else {
                position++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }

    //跳过特殊符号
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80 || c>0x9FFF);
    }

    //将一个敏感词添加到前缀树
    private void addKeywoed(String  keyword){
        TrieNode tempNode = rootNode;
        for (int i=0;i<keyword.length();i++){
            //获取当前字符
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode==null){
                //初始化子节点
                subNode = new TrieNode();
                //说明字节点没有这个字符，需要添加到字节点
                tempNode.addSubNode(c, subNode);
            }
            //指向字节点，进入下一轮循环
            tempNode = subNode;

            //当循环到最后一个字符的时候，给最后一个字符打一个标记
            if (i == keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }






    //定义数据结构（前缀树）
    private class TrieNode{
        /**
         * 前缀树中有2个属性：1个是标识符，1个是字节点
         */
        private boolean isKeywordEnd = false;
        private Map<Character ,TrieNode> subNode = new HashMap<>();
        //定义添加子节点的方法
        private void addSubNode(Character c,TrieNode node){
            subNode.put(c, node);
        }
        //定义获取字节点的方法
        private TrieNode getSubNode(Character character){
            return subNode.get(character);
        }

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
    }
}
