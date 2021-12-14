package com.yjxxt.crm.config;

import com.yjxxt.crm.interceptors.NoLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Bean
    public NoLoginInterceptor noLoginInterceptor() {
        return new NoLoginInterceptor();
    }
    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
// 需要一个实现HandlerInterceptor接口的拦截器实例，这里使用的是NoLoginInterceptor
        registry.addInterceptor(noLoginInterceptor())
// 用于设置拦截器的过滤路径规则
                .addPathPatterns("/**")
// 用于设置不需要拦截的过滤规则
                .excludePathPatterns("/index", "/user/login", "/css/**", "/images/**", "/js/**", "/lib/**");
    }
}
