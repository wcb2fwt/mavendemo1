package com.wymx.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求参数
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+": "+value);
        }
        //请求体
        //get请求直接在浏览器地址栏后面拼 ？：代表是有参数的  code：代表参数名
        System.out.println(request.getParameter("code"));

        //response 用来返回相应数据
        //需要设置一下返回的数据类型，你是要返回网页呢？、普通字符串、图片还是什么？

        //比如我要返回一个网页
        response.setContentType("text/html;charset=utf-8");
        //用response向浏览器响应网页其实就是通过它里面封装的输出流向浏览器输出
        try (
                PrintWriter writer = response.getWriter();
                ){
            writer.write("<h1>牛克网<h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //处理浏览器请求更方便的方式
    /**
     * 处理浏览器请求我们分2各方面：一个是我要接收请求数据，基于request。一个是我要向浏览器返回响应数据，基于response
     * 因此我们需要学习2个方面，怎么接受请求参数，怎么返回响应数据？
     */

    /**
     * get请求  get是获取某些数据，默认的请求方式也是get请求
     * 当我们希望从浏览器获取某些数据时可以使用get请求
     */

    /**
     * 假设我要查询所有的学生，学生很多，我们需要分页显示，分页的时候就要带上一些条件了
     * current：当前是第几页？
     * limit：每页显示多少数据？
     * /students？current=1&limit=20
     */
    @RequestMapping(path = "students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current",required = false,defaultValue = "1") int current,
            @RequestParam(name = "limit",required = false,defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    /**
     * 我们要查询一个学生，将这个学生的编号编排在路径中
     * /student/123
     * 当编排在路径当中后就不是上面这样获取了
     */
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "one student";
    }


    /**
     * post请求：浏览器想要向服务器提交数据，先得打开一个有提交表单的网页
     * get请求传参，一个是在明面上，另一个是长度有限，所以我们一般提交数据都用post请求
     */
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //如何向浏览器返回相应数据？
    /**
     * 响应html数据
     * 假设浏览器需要查询一个老师，服务器帮它查询到了一个老师，就要把这个数据响应给浏览器，我们响应的是一个网页的形式
     */
    //返回html不需要加@ResponseBody了，不加默认返回的就是html

    /**
     * ModelAndView:
     *
     */
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","zhangsan");
        mav.addObject("age",30);
        mav.setViewName("/demo/view");
        return mav;
    }

    //查询学校

    /**
     *
     * @param model dispatcherServlet 持有这个对象的引用，我们在方法内部给model存数据，dispatcherServlet也是可以得到的
     *
     * @return
     */
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age",90);
        return "/demo/view";
    }


    /**
     * 响应json数据，一般是在异步请求中，当前网页不刷新，悄悄的访问服务器
     * 当我们要把java对象返回给浏览器，浏览器解析这个对象用的是js
     * 我们不能将java对象 转换为 js对象，我们使用json可以实现两者的兼容
     * json是具有特定格式的字符串，我们可以将java对象转换为 json字符串 给浏览器传过去，浏览器可以将json字符串 转换为 js对象。
     * 要返回json数据得加上@ResponseBody注解，不加以为返回的是html
     */
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String ,Object> getEmp(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", 77);
        map.put("salary", 8000.00);
        return map;
    }

    //返回所有员工
    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String ,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", 23);
        map.put("salary", 8000.00);
        list.add(map);
        map = new HashMap<>();
        map.put("name", "李四");
        map.put("age", 27);
        map.put("salary", 28000.00);
        list.add(map);
        map = new HashMap<>();
        map.put("name", "王五");
        map.put("age", 21);
        map.put("salary", 18000.00);
        list.add(map);
        return list;
    }


}
