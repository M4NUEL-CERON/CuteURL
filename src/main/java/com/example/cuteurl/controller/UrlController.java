package com.example.cuteurl.controller;

import com.example.cuteurl.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shorten(@RequestBody Map<String, String> body) {
        String originalUrl = body.get("url");
        //  peticiones sin URL para evitar guardar claves vacías en Redis
        if (originalUrl == null || originalUrl.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        String shortUrl = urlService.shorten(originalUrl);
        return ResponseEntity.ok(Map.of("shortUrl", shortUrl));
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String originalUrl = urlService.resolve(code);
        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }
        // 302 (Found) para que los navegadores no cacheen la redirección
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}