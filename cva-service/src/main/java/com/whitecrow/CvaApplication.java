package com.whitecrow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import javax.annotation.Resource;


/**
 * @author WhiteCrow
 */
@SpringBootApplication(scanBasePackages = {"com.whitecrow"})
@MapperScan("com.whitecrow.**.mapper")
public class CvaApplication {
    public static void main(String[] args) {
        SpringApplication.run(CvaApplication.class, args);
    }

}
