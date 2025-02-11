package com.nearx.image_overlay.service;

import com.nearx.image_overlay.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@Service
public class ApiService {

    private static final String API_URL = "http://localhost:8080/data"; 

    private final RestTemplate restTemplate;

    @Autowired
    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ApiResponse> getApiData() {
        try {
            ApiResponse[] responseArray = restTemplate.getForObject(API_URL, ApiResponse[].class);


            if (responseArray != null) {
                return List.of(responseArray);
            } else {
                return List.of(); 
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {

            System.err.println("Erro ao consumir a API: " + e.getMessage());
            return List.of(); 
        } catch (Exception e) {

            System.err.println("Erro desconhecido: " + e.getMessage());
            return List.of(); 
        }
    }
}