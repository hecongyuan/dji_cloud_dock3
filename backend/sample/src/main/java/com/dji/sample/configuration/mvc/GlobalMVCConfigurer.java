package com.dji.sample.configuration.mvc;

import com.dji.sample.component.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class GlobalMVCConfigurer implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    private static List<String> excludePaths = new ArrayList<>();

    @Value("${url.manage.prefix}")
    private String managePrefix;

    @Value("${url.manage.version}")
    private String manageVersion;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 为所有路径配置CORS，包括代理路径
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Exclude the login interface.
        excludePaths.add("/" + managePrefix + manageVersion + "/login");
        excludePaths.add("/" + managePrefix + manageVersion + "/token/refresh");
        excludePaths.add("/swagger-ui.html");
        excludePaths.add("/swagger-ui/**");
        excludePaths.add("/v3/**");
        excludePaths.add("/ui/**");
        // 排除代理路径，避免认证拦截
        excludePaths.add("/proxy/**");
        // Intercept for all request interfaces.
        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns(excludePaths);
    }
}
