package com.example.cuteurl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
//observacion
//logica para evitar que en los codigos se repitan en algun momento (falta imprementar)
@Service
public class UrlService {

    private final StringRedisTemplate redis;
    private final String baseUrl;

    public UrlService(StringRedisTemplate redis, @Value("${app.base-url}") String baseUrl) {
        this.redis = redis;
        this.baseUrl = baseUrl;
    }
 //aquí esta la logica que crea el codigo unico y guardar la clave valor en redis cloud
    public String shorten(String originalUrl) {
        String code = UUID.randomUUID().toString().substring(0, 6);
        redis.opsForValue().set("url:" + code, originalUrl);
        return baseUrl + "/" + code;
    }

    public String resolve(String code) {
        return redis.opsForValue().get("url:" + code);
    }
}