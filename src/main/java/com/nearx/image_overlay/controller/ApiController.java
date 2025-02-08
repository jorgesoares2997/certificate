package com.nearx.image_overlay.controller;

import com.nearx.image_overlay.model.ApiResponse;
import com.nearx.image_overlay.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiController {

    private final ApiService apiService;

    @Autowired
    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/api/data")
    public List<ApiResponse> getApiData() {
        // Chama o serviço para consumir a API e retornar os dados
        return apiService.getApiData();
    }
}