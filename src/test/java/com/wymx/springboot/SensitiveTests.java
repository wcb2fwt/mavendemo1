package com.wymx.springboot;

import com.wymx.springboot.util.SensiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveTests {

    @Autowired
    private SensiveFilter sensiveFilter;

    @Test
    public void setSensiveFilter(){
        String text ="这里可以赌博，可以嫖娼，可以吸毒，可以开票，你是习近平吗？";
        String filter = sensiveFilter.filter(text);
        System.out.println(filter);
    }

}
