package com.nearx.image_overlay.model;

public class ApiResponse {

    private String name;
    private String course;
    private String date;
    private String workload;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWorkload() {
        return workload;
    }

    public void setWorkload(String workload) {
        this.workload = workload;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "name='" + name + '\'' +
                ", course='" + course + '\'' +
                ", date='" + date + '\'' +
                ", workload='" + workload + '\'' +
                '}';
    }
}