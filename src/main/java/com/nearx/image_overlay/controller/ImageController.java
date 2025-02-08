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
    public ResponseEntity<?> generateImage() {
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

            // Verifica se a lista está vazia ou se não há dados válidos
            if (dynamicTextsList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Todos os certificados já foram gerados para os nomes e cursos fornecidos.");
            }

            // Definir o número máximo de tentativas
            int maxRetries = dynamicTextsList.size();
            Map<String, String> dynamicTexts = null;
            int attempt = 0;

            while (attempt < maxRetries) {
                dynamicTexts = dynamicTextsList.get(attempt);

                // Tente gerar a imagem
                try {
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

                    // Geração da imagem e manipulação das strings
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

                    String courseTop = dynamicTexts.getOrDefault("course", "Curso").toUpperCase();
                    g2d.setFont(new Font("Arial", Font.PLAIN, 85));
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(courseTop, 320, 410);

                    String courseCenter = dynamicTexts.getOrDefault("course", "Curso");
                    g2d.setFont(new Font("Arial", Font.BOLD, 100));
                    g2d.setColor(Color.decode("#2A5FEF"));
                    g2d.drawString(courseCenter, 1200, 1500);

                    g2d.dispose();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", baos);
                    byte[] imageBytes = baos.toByteArray();

                    // Verifica se a imagem com o mesmo nome e curso já existe
                    String course = dynamicTexts.getOrDefault("course", "Curso").toUpperCase();
                    GeneratedImage existingImage = imageRepository.findByNameAndCourse(name, course).orElse(null);

                    if (existingImage != null) {
                        // Se a imagem já existir, retorna a imagem existente
                        return new ResponseEntity<>(existingImage.getImageData(), HttpStatus.OK);
                    }

                    // Se não existir, cria e salva a nova imagem
                    GeneratedImage savedImage = imageRepository.save(new GeneratedImage(imageBytes, name, course));

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Type", "image/png");

                    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

                } catch (Exception e) {
                    // Em caso de erro (500), tenta o próximo item da lista
                    attempt++;
                    if (attempt == maxRetries) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Todos os certificados já foram gerados para os nomes e cursos fornecidos.");
                    }
                }
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar todos os certificados.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao gerar a imagem.");
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