package com.nearx.image_overlay.controller;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nearx.image_overlay.dto.ImageOverlayRequest;
import com.nearx.image_overlay.model.GeneratedImage;
import com.nearx.image_overlay.repository.GeneratedImageRepository;
import com.nearx.image_overlay.service.EmailService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
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
    public ResponseEntity<?> generateImage() {
        try {
            ImageOverlayRequest request = loadConfig();

            URL apiUrl = new URL("http://localhost:8080/data");
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> dynamicTextsList = objectMapper.readValue(apiUrl.openStream(),
                    new TypeReference<>() {
                    });

            if (dynamicTextsList.isEmpty()) {
                return ResponseEntity.badRequest().body("Nenhum certificado disponível para geração.");
            }

            for (Map<String, String> dynamicTexts : dynamicTextsList) {
                try {
                    String name = dynamicTexts.getOrDefault("name", "Nome");
                    String course = dynamicTexts.getOrDefault("course", "Curso");

                    if (imageRepository.findByNameAndCourse(name, course).isPresent()) {
                        continue;
                    }
                    BufferedImage image;
                    try (InputStream imageStream = new URL(request.getImageUrl()).openStream()) {
                        image = ImageIO.read(imageStream);
                    }

                    Graphics2D g2d = image.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int centerX = image.getWidth() / 2;

                    desenharTexto(g2d, name, "Arial", Font.BOLD, 120, Color.BLACK, centerX, 1140);
                    desenharTexto(g2d, dynamicTexts.getOrDefault("date", "XX/XX/XX"), "Arial", Font.PLAIN, 85,
                            Color.BLACK, centerX, 2200);
                    desenharTexto(g2d, course, "Arial", Font.BOLD, 100, Color.decode("#2A5FEF"), centerX, 1500);
                    String workload = dynamicTexts.getOrDefault("workLoad", "XX");
                    g2d.setFont(new Font("Arial", Font.PLAIN, 85));
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(workload, 510, 2200);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 85));
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(course, 320, 410);
                    desenharTexto(g2d, dynamicTexts.getOrDefault("certificate", "Certificamos que"), "Arial",
                            Font.PLAIN, 100, Color.decode("#2A5FEF"), centerX, 880);
                    desenharTexto(g2d, dynamicTexts.getOrDefault("concluded", "Concluiu com mérito o curso"), "Arial",
                            Font.PLAIN, 100, Color.decode("#2A5FEF"), centerX, 1390);
                    desenharTexto(g2d, dynamicTexts.getOrDefault("nearx", "da NearX Educational Platform"), "Arial",
                            Font.PLAIN, 100, Color.decode("#2A5FEF"), centerX, 1610);

                    g2d.dispose();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", baos);
                    byte[] imageBytes = baos.toByteArray();

                    GeneratedImage savedImage = imageRepository.save(new GeneratedImage(imageBytes, name, course));

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Type", "image/png");

                    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar certificados.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao carregar configuração.");
        }
    }

    private void desenharTexto(Graphics2D g2d, String texto, String fonte, int estilo, int tamanho, Color cor, int x,
            int y) {
        Font font = new Font(fonte, estilo, tamanho);
        g2d.setFont(font);
        g2d.setColor(cor);
        FontMetrics metrics = g2d.getFontMetrics(font);
        int textWidth = metrics.stringWidth(texto);
        g2d.drawString(texto, x - (textWidth / 2), y);
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

    @PostMapping("/send-email/{id}")
    public ResponseEntity<String> sendImageByEmail(@PathVariable Long id, @RequestParam String email) {
        try {
            GeneratedImage generatedImage = imageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));

            emailService.sendEmailWithImage(email, generatedImage.getImageData());

            return ResponseEntity.ok("E-mail enviado com sucesso para: " + email);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao enviar o e-mail: " + e.getMessage());
        }
    }
}