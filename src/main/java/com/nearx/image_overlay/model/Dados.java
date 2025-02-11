package com.nearx.image_overlay.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "dados", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name", "course", "email" }) 
})
public class Dados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String course;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String workLoad;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, unique = true) 
    private String email;


    public Dados() {
    }


    public Dados(Long id, String course, String name, String workLoad, LocalDate date, String email) {
        this.id = id;
        this.course = course;
        this.name = name;
        this.workLoad = workLoad;
        this.date = date;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getCourse() {
        return course;
    }

    public String getName() {
        return name;
    }

    public String getWorkLoad() {
        return workLoad;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWorkLoad(String workLoad) {
        this.workLoad = workLoad;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}