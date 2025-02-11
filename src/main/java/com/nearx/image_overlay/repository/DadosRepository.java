package com.nearx.image_overlay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nearx.image_overlay.model.Dados;

@Repository
public interface DadosRepository extends JpaRepository<Dados, Long> {
    boolean existsByNameAndCourseAndEmail(String name, String course, String email);

    Optional<Dados> findByEmail(String email);
}