package com.dji.sample.manage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * 代理控制器，用于转发请求到外部服务，解决 CORS 问题
 * @author sean.zhou
 * @version 0.1
 * @date 2024/01/01
 */
@RestController
@RequestMapping("/proxy")
@CrossOrigin(origins = "*", allowCredentials = "false") // 禁用Spring Boot的自动CORS处理
@Slf4j
public class ProxyController {

    @Autowired
    private RestTemplate restTemplate;



    /**
     * 代理 ZLM WebRTC API 请求
     * @param request 原始请求
     * @return 代理响应
     */
    @RequestMapping(value = {"/rtc/**", "/index/api/webrtc"}, method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
    public ResponseEntity<String> proxyRtcRequest(HttpServletRequest request) {
        try {
            // 构建目标 URL
            String requestURI = request.getRequestURI();
            String targetUrl;
            
            if (requestURI.startsWith("/proxy/index/api/webrtc")) {
                // 代理ZLM WebRTC API
                String baseUrl = "http://124.71.163.191:8088" + requestURI.replace("/proxy/index/api/webrtc", "/index/api/webrtc");
                // 获取原始查询字符串
                String queryString = request.getQueryString();
                // 添加ZLM认证secret
                String secret = "45pIp0ynoyG6KIZCgeLTw6qzsHU2XjEl";
                if (queryString != null && !queryString.isEmpty()) {
                    targetUrl = baseUrl + "?" + queryString + "&secret=" + secret;
                } else {
                    targetUrl = baseUrl + "?secret=" + secret;
                }
            } else {
                // 兼容旧的RTC路径
                targetUrl = "http://124.71.163.191:8088" + requestURI.replace("/proxy/rtc", "/rtc");
            }
            
            log.info("Proxying request from {} to {}", requestURI, targetUrl);
            log.info("Query string: {}", request.getQueryString());

            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                // 跳过一些不应该转发的头部
                if (!"host".equalsIgnoreCase(headerName) && 
                    !"content-length".equalsIgnoreCase(headerName)) {
                    headers.set(headerName, headerValue);
                }
            }

            // 读取请求体
            String requestBody = null;
            if ("POST".equalsIgnoreCase(request.getMethod()) || 
                "PUT".equalsIgnoreCase(request.getMethod())) {
                try {
                    requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
                    log.debug("Request body: {}", requestBody);
                } catch (IOException e) {
                    log.warn("Failed to read request body", e);
                }
            }

            // 创建 HTTP 实体
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                targetUrl,
                HttpMethod.valueOf(request.getMethod()),
                entity,
                String.class
            );

            // 设置响应头 - 复制所有头部，让Spring Boot的CORS配置处理CORS
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.putAll(response.getHeaders());

            log.info("Proxy response status: {}", response.getStatusCode());
            return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());

        } catch (Exception e) {
            log.error("Proxy request failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Proxy request failed: " + e.getMessage());
        }
    }


} 