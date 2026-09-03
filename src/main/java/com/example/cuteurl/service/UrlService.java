package com.example.cuteurl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class UrlService {

    private final StringRedisTemplate redis;
    private final String baseUrl;

    public UrlService(StringRedisTemplate redis, @Value("${app.base-url}") String baseUrl) {
        this.redis = redis;
        this.baseUrl = baseUrl;
    }
    public String shorten(String originalUrl) {
        String code;
        // setIfAbsent es atómico (SETNX): reintenta si el código ya existe en Redis
        do {
            code = UUID.randomUUID().toString().substring(0, 6);
        } while (Boolean.FALSE.equals(redis.opsForValue().setIfAbsent("url:" + code, originalUrl)));
        return baseUrl + "/" + code;
    }

    public String resolve(String code) {
        return redis.opsForValue().get("url:" + code);
    }
}