package com.nearx.image_overlay.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "certificate")
public class GeneratedImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "image_data", nullable = false)
    private byte[] imageData;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "course", nullable = false)
    private String course;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Construtores
    public GeneratedImage() {
    }

    public GeneratedImage(byte[] imageData, String name, String course) {
        this.imageData = imageData;
        this.name = name;
        this.course = course;
        this.createdAt = LocalDateTime.now();
    }

    public GeneratedImage(Long id, byte[] imageData, String name, String course, LocalDateTime createdAt) {
        this.id = id;
        this.imageData = imageData;
        this.name = name;
        this.course = course;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString, equals e hashCode podem ser implementados aqui também
}