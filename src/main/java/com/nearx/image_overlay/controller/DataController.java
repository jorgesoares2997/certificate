package com.nearx.image_overlay.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nearx.image_overlay.model.Dados;
import com.nearx.image_overlay.service.DataService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/data")
public class DataController {

    private final DataService dataService;


    public DataController(DataService dataService) {
        this.dataService = dataService;
    }


    @GetMapping
    public ResponseEntity<List<Dados>> getAllData() {
        List<Dados> dados = dataService.listDados();
        return ResponseEntity.ok(dados);
    }


    @PostMapping
    public ResponseEntity<?> createDados(@RequestBody Dados dados) {
        try {
            Dados novoDado = dataService.saveData(dados);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoDado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a solicitação.");
        }
    }
}