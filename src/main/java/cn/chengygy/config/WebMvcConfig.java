package cn.chengygy.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Slf4j
@Configuration
@EnableSwagger2
@EnableKnife4j
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");
        log.info("前端访问：http://localhost:8080/front/index.html");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
}
