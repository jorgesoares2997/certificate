package com.nearx.image_overlay.dto;

import java.awt.*;

public class TextOverlay {
    private String text;
    private int x;
    private int y;
    private int fontSize;
    private String color;
    private boolean bold;


    public TextOverlay() {
    }

    public TextOverlay(String text, int x, int y, int fontSize, String color, boolean bold) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.fontSize = fontSize;
        this.color = color;
        this.bold = bold;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

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