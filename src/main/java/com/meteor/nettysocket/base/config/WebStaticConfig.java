package com.meteor.nettysocket.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * created by yebinghuan
 *
 */
@Configuration
public class WebStaticConfig extends WebMvcConfigurerAdapter{

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置模板资源路径

        registry.addResourceHandler("/**").addResourceLocations("classpath:/webapp/");
//        registry.addResourceHandler("/").addResourceLocations("classpath:/webapp");
        super.addResourceHandlers(registry);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //默认界面
        registry.addViewController("").setViewName( "forward:/index.html" );
    }
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/");
        viewResolver.setRedirectHttp10Compatible(false);
        viewResolver.setContentType("text/html;charset=UTF-8");

        viewResolver.setViewClass(JstlView.class);
        return viewResolver;
    }

}
