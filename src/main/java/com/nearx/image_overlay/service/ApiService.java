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

    private static final String API_URL = "http://localhost:8080/data"; // URL da sua API

    private final RestTemplate restTemplate;

    @Autowired
    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ApiResponse> getApiData() {
        try {
            // Fazendo a requisição GET e retornando os dados como uma lista de ApiResponse
            ApiResponse[] responseArray = restTemplate.getForObject(API_URL, ApiResponse[].class);

            // Verifica se o array não é nulo, caso contrário retorna uma lista vazia
            if (responseArray != null) {
                return List.of(responseArray);
            } else {
                return List.of(); // Retorna uma lista vazia em caso de resposta nula
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // Erros de cliente ou servidor
            System.err.println("Erro ao consumir a API: " + e.getMessage());
            return List.of(); // Retorna uma lista vazia em caso de erro
        } catch (Exception e) {
            // Qualquer outro erro genérico
            System.err.println("Erro desconhecido: " + e.getMessage());
            return List.of(); // Retorna uma lista vazia em caso de erro
        }
    }
}