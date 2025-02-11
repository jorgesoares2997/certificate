package com.nearx.image_overlay.service;

import org.springframework.stereotype.Service;
import com.nearx.image_overlay.model.Dados;
import com.nearx.image_overlay.repository.DadosRepository;
import java.util.List;
import java.util.Optional;

@Service
public class DataService {

    private final DadosRepository dadosRepository;


    public DataService(DadosRepository dadosRepository) {
        this.dadosRepository = dadosRepository;
    }

    public List<Dados> listDados() {
        return dadosRepository.findAll();
    }

    public Dados saveData(Dados dados) {
        if (dadosRepository.existsByNameAndCourseAndEmail(dados.getName(), dados.getCourse(), dados.getEmail())) {
            throw new IllegalArgumentException("Já existe um certificado com o mesmo nome, curso e email no banco.");
        }
        return dadosRepository.save(dados);
    }

    public Optional<Dados> findByEmail(String email) {
        return dadosRepository.findByEmail(email);
    }
}