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
        // 6 caracteres del UUID dan ~2 billones de combinaciones posibles
        String code = UUID.randomUUID().toString().substring(0, 6);
        // prefijo "url:" para agrupar las claves en Redis y evitar colisiones
        redis.opsForValue().set("url:" + code, originalUrl);
        return baseUrl + "/" + code;
    }

    public String resolve(String code) {
        return redis.opsForValue().get("url:" + code);
    }
}