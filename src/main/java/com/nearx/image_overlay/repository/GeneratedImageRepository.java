package com.nearx.image_overlay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nearx.image_overlay.model.GeneratedImage;

public interface GeneratedImageRepository extends JpaRepository<GeneratedImage, Long> {
    List<GeneratedImage> findByNameOrCourse(String name, String course);

}