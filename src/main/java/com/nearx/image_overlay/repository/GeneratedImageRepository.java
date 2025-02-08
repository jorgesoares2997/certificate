package com.nearx.image_overlay.repository;

import com.nearx.image_overlay.model.GeneratedImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeneratedImageRepository extends JpaRepository<GeneratedImage, Long> {
    Optional<GeneratedImage> findByNameAndCourse(String name, String course);
}