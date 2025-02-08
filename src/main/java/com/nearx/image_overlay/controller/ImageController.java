package com.nearx.image_overlay.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Map;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nearx.image_overlay.dto.ImageOverlayRequest;
import com.nearx.image_overlay.dto.TextOverlay;
import com.nearx.image_overlay.model.GeneratedImage;
import com.nearx.image_overlay.repository.GeneratedImageRepository;
import com.nearx.image_overlay.service.EmailService;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    private static final String CONFIG_PATH = "src/main/resources/config.json";
    private final EmailService emailService;
    private final GeneratedImageRepository imageRepository;

    @Autowired
    public ImageController(GeneratedImageRepository imageRepository, EmailService emailService) {
        this.imageRepository = imageRepository;
        this.emailService = emailService;
    }

    private ImageOverlayRequest loadConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(CONFIG_PATH), ImageOverlayRequest.class);
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateImage() {
        try {
            // Carrega a configuração
            ImageOverlayRequest request = loadConfig();

            // Busca os dados diretamente da URL da API
            URL apiUrl = new URL("http://localhost:8080/data"); // Substitua pela URL da API
            ObjectMapper objectMapper = new ObjectMapper();

            // Supondo que a resposta seja um array de objetos
            List<Map<String, String>> dynamicTextsList = objectMapper.readValue(apiUrl,
                    new TypeReference<List<Map<String, String>>>() {
                    });

            // Use o primeiro item da lista se a resposta for um array com um único objeto
            Map<String, String> dynamicTexts = dynamicTextsList.get(0);

            // O restante do código permanece o mesmo
            InputStream imageStream = new URL(request.getImageUrl()).openStream();
            BufferedImage image = ImageIO.read(imageStream);
            imageStream.close();

            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Aplique as sobreposições de texto do request (se houver)
            for (TextOverlay overlay : request.getTexts()) {
                if (dynamicTexts.containsKey(overlay.getText())) {
                    overlay.setText(dynamicTexts.get(overlay.getText()));
                }

                int fontStyle = overlay.isBold() ? Font.BOLD : Font.PLAIN;
                g2d.setFont(new Font("Arial", fontStyle, overlay.getFontSize()));
                g2d.setColor(overlay.getColorAsAWT());
                g2d.drawString(overlay.getText(), overlay.getX(), overlay.getY());
            }

            // Desenha os campos específicos (name, workload, etc.)
            String name = dynamicTexts.getOrDefault("name", "Nome");
            g2d.setFont(new Font("Arial", Font.BOLD, 120));
            g2d.setColor(Color.BLACK);
            g2d.drawString(name, 1500, 1140);

            String workload = dynamicTexts.getOrDefault("workload", "XX");
            g2d.setFont(new Font("Arial", Font.PLAIN, 85));
            g2d.setColor(Color.BLACK);
            g2d.drawString(workload, 510, 2200);

            String date = dynamicTexts.getOrDefault("date", "XX/XX/XX");
            g2d.setFont(new Font("Arial", Font.PLAIN, 85));
            g2d.setColor(Color.BLACK);
            g2d.drawString(date, 1540, 2200);

            String course = dynamicTexts.getOrDefault("course", "Curso").toUpperCase();
            g2d.setFont(new Font("Arial", Font.PLAIN, 85));
            g2d.setColor(Color.BLACK);
            g2d.drawString(course, 320, 410);

            String courseCenter = dynamicTexts.getOrDefault("course", "Curso");
            g2d.setFont(new Font("Arial", Font.BOLD, 100));
            g2d.setColor(Color.decode("#2A5FEF"));
            g2d.drawString(courseCenter, 1200, 1500);

            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao enviar o e-mail: " + e.getMessage());
        }
    }

}