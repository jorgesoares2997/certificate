package com.nearx.image_overlay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageOverlayRequest {
    private String imageUrl;
    private List<TextOverlay> texts;
}