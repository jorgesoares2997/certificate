package com.nearx.image_overlay.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "generated_images")
public class GeneratedImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "image_data", nullable = false)
    private byte[] imageData;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Construtores
    public GeneratedImage() {
    }

    public GeneratedImage(byte[] imageData) {
        this.imageData = imageData;
        this.createdAt = LocalDateTime.now();
    }

    public GeneratedImage(Long id, byte[] imageData, LocalDateTime createdAt) {
        this.id = id;
        this.imageData = imageData;
        this.createdAt = createdAt;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString, equals e hashCode podem ser implementados aqui também
}