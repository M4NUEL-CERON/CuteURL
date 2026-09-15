package com.example.cuteurl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Service
public class UrlService {

    private final StringRedisTemplate redis;
    private final String baseUrl;
    private final Duration ttl;

    public UrlService(
            StringRedisTemplate redis,
            @Value("${app.base-url}") String baseUrl,
            @Value("${app.url-ttl-days:30}") int ttlDays) {
        this.redis   = redis;
        this.baseUrl = baseUrl;
        this.ttl     = Duration.ofDays(ttlDays);
    }

    public boolean isValidUrl(String url) {
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            return (scheme != null && (scheme.equals("http") || scheme.equals("https")))
                    && uri.getHost() != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String shorten(String originalUrl) {
        String code;
        // setIfAbsent es atómico (SETNX): reintenta si el código ya existe en Redis
        do {
            code = UUID.randomUUID().toString().substring(0, 6);
        } while (Boolean.FALSE.equals(redis.opsForValue().setIfAbsent("url:" + code, originalUrl, ttl)));
        return baseUrl + "/" + code;
    }

    public String resolve(String code) {
        return redis.opsForValue().get("url:" + code);
    }
}