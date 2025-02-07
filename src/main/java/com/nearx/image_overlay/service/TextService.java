package com.nearx.image_overlay.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class TextService {

    private static final String API_URL = "https://api.exemplo.com/texts"; // URL da API

    public Map<String, String> getDynamicTexts() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject(API_URL, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(); // Retorna um mapa vazio em caso de erro
        }
    }
}