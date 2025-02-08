package com.nearx.image_overlay.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nearx.image_overlay.model.GeneratedImage;
import com.nearx.image_overlay.repository.GeneratedImageRepository;

@Service
public class GeneratedImageService {

    private final GeneratedImageRepository repository;

    public GeneratedImageService(GeneratedImageRepository repository) {
        this.repository = repository;
    }

    public GeneratedImage saveGeneratedImage(byte[] imageData, String name, String course) {
        Optional<GeneratedImage> existingRecords = repository.findByNameAndCourse(name, course);

        // Verifica se algum registro já tem exatamente o mesmo name e course
        boolean alreadyExists = existingRecords.stream()
                .anyMatch(record -> record.getName().equals(name) && record.getCourse().equals(course));

        if (alreadyExists) {
            throw new IllegalArgumentException("Já existe um certificado com o mesmo nome e curso!");
        }

        GeneratedImage newImage = new GeneratedImage(imageData, name, course);
        return repository.save(newImage);
    }
}