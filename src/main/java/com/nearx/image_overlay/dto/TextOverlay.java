package com.nearx.image_overlay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextOverlay {
    private String text;
    private int x;
    private int y;
    private int fontSize;
    private String color;
    private boolean bold;

    public Color getColorAsAWT() {
        try {
            return Color.decode(color);
        } catch (NumberFormatException e) {
            try {
                return (Color) Color.class.getField(color.toUpperCase()).get(null);
            } catch (Exception ex) {
                return Color.BLACK;
            }
        }
    }
}