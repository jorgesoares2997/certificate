package com.nearx.image_overlay.dto;

import java.util.List;

public class ImageOverlayRequest {

    private String imageUrl;
    private List<TextOverlay> texts;

    // Construtores
    public ImageOverlayRequest() {
    }

    public ImageOverlayRequest(String imageUrl, List<TextOverlay> texts) {
        this.imageUrl = imageUrl;
        this.texts = texts;
    }

    // Getters e Setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<TextOverlay> getTexts() {
        return texts;
    }

    public void setTexts(List<TextOverlay> texts) {
        this.texts = texts;
    }
}