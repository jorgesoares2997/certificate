package com.nearx.image_overlay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nearx.image_overlay.dto.ImageOverlayRequest;
import com.nearx.image_overlay.dto.TextOverlay;
import com.nearx.image_overlay.service.TextService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Map;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    private static final String CONFIG_PATH = "src/main/resources/config.json"; // Caminho do JSON
    private final TextService textService;

    public ImageController(TextService textService) {
        this.textService = textService;
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateImage() {
        try {
            ImageOverlayRequest request = loadConfig();
            Map<String, String> dynamicTexts = textService.getDynamicTexts(); // Busca textos dinâmicos

            InputStream imageStream = new URL(request.getImageUrl()).openStream();
            BufferedImage image = ImageIO.read(imageStream);
            imageStream.close();

            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (TextOverlay overlay : request.getTexts()) {
                // Se a API forneceu um novo texto para este elemento, substituímos
                if (dynamicTexts.containsKey(overlay.getText())) {
                    overlay.setText(dynamicTexts.get(overlay.getText()));
                }

                int fontStyle = overlay.isBold() ? Font.BOLD : Font.PLAIN;
                g2d.setFont(new Font("Arial", fontStyle, overlay.getFontSize()));
                g2d.setColor(overlay.getColorAsAWT());

                FontMetrics fm = g2d.getFontMetrics();
                int x = overlay.getX() - (fm.stringWidth(overlay.getText()) / 2);
                int y = overlay.getY() + (fm.getAscent() / 2);

                g2d.drawString(overlay.getText(), x, y);
            }
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "image/png");

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private ImageOverlayRequest loadConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(CONFIG_PATH), ImageOverlayRequest.class);
    }
}