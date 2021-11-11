package cn.edu.just.moocweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginConfig implements WebMvcConfigurer {

    @Bean
    public MoocWebInterceptor getMoocWebInterceptor(){
        return new MoocWebInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        InterceptorRegistration registration = registry.addInterceptor(getMoocWebInterceptor());
        registration.addPathPatterns("/**");
        registration.excludePathPatterns(
                "/loginPage",
                "/login",
                "/register",
                "/",
                "/error",
                "/regPage",
                "/**/*.js",
                "/**/*.css",
                "/**/*.woff",
                "/**/*.ttf",
                "/**/*.jpg",
                "/**/*.png",
                "/**/*.ico",
                "/**/*.svg",
                "/**/*.map"
        );
    }
}
