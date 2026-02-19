package com.dji.sample.component;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.dji.sample.component.AuthInterceptor.PARAM_TOKEN;

/**
 * @author sean.zhou
 * @version 0.1
 * @date 2021/11/22
 */
@Component
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        // 获取请求的 Origin
        String origin = req.getHeader("Origin");
        
        // 设置 CORS 头部
        if (origin != null) {
            res.addHeader("Access-Control-Allow-Origin", origin);
        } else {
            res.addHeader("Access-Control-Allow-Origin", "*");
        }
        
        res.addHeader("Access-Control-Allow-Credentials", "true");
        res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, PATCH");
        res.addHeader("Access-Control-Allow-Headers", 
            "Access-Control-Allow-Headers, " +
            "Access-Control-Allow-Origin, " +
            "Access-Control-Allow-Methods, " +
            "Access-Control-Allow-Credentials, " +
            "Authorization, " +
            "Content-Length, " +
            "X-CSRF-Token, " +
            "Token, " +
            "session, " +
            "X_Requested_With, " +
            "Accept, " +
            "Origin, " +
            "Host, " +
            "Connection, " +
            "Accept-Encoding, " +
            "Accept-Language, " +
            "DNT, " +
            "X-CustomHeader, " +
            "Keep-Alive, " +
            "User-Agent, " +
            "X-Requested-With, " +
            "If-Modified-Since, " +
            "Cache-Control, " +
            "Content-Type, " +
            "Pragma, " +
            PARAM_TOKEN);
        res.addHeader("Access-Control-Max-Age", "3600");
        res.addHeader("Access-Control-Expose-Headers", "Content-Length, Access-Control-Allow-Origin, Access-Control-Allow-Headers");
        
        // 处理预检请求
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}