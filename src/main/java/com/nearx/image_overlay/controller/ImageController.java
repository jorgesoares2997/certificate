package com.nearx.image_overlay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nearx.image_overlay.dto.ImageOverlayRequest;
import com.nearx.image_overlay.dto.TextOverlay;
import com.nearx.image_overlay.model.GeneratedImage;
import com.nearx.image_overlay.repository.GeneratedImageRepository;
import com.nearx.image_overlay.service.EmailService;
import com.nearx.image_overlay.service.TextService;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String CONFIG_PATH = "src/main/resources/config.json";
    private final TextService textService;
    private final GeneratedImageRepository imageRepository;
    private final EmailService emailService;

    // Injetando o serviço de email
    @Autowired
    public ImageController(TextService textService, GeneratedImageRepository imageRepository, EmailService emailService) {
        this.textService = textService;
        this.imageRepository = imageRepository;
        this.emailService = emailService;
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateImage() {
        try {
            ImageOverlayRequest request = loadConfig();
            Map<String, String> dynamicTexts = textService.getDynamicTexts();

            InputStream imageStream = new URL(request.getImageUrl()).openStream();
            BufferedImage image = ImageIO.read(imageStream);
            imageStream.close();

            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (TextOverlay overlay : request.getTexts()) {
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

            // Salva a imagem no banco de dados
            GeneratedImage savedImage = imageRepository.save(new GeneratedImage(imageBytes));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "image/png");

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/retrieve/{id}")
    public ResponseEntity<byte[]> retrieveImage(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(image -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Type", "image/png");
                    return new ResponseEntity<>(image.getImageData(), headers, HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Novo endpoint para enviar a imagem por e-mail
    @PostMapping("/send-email/{id}")
    public ResponseEntity<String> sendImageByEmail(@PathVariable Long id, @RequestParam String email) {
        try {
            // Recupera a imagem do banco de dados
            GeneratedImage generatedImage = imageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));

            // Envia o e-mail com a imagem
            emailService.sendEmailWithImage(email, generatedImage.getImageData());

            return ResponseEntity.ok("E-mail enviado com sucesso para: " + email);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao enviar o e-mail: " + e.getMessage());
        }
    }

    private ImageOverlayRequest loadConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(CONFIG_PATH), ImageOverlayRequest.class);
    }
}