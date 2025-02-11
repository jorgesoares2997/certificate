package com.nearx.image_overlay.controller;

import com.nearx.image_overlay.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public String sendEmail(@RequestParam String email, @RequestBody String base64Image) {
        try {
            base64Image = base64Image.replaceAll("\\s", "");
            int paddingCount = 4 - (base64Image.length() % 4);
            if (paddingCount < 4) {
                base64Image = base64Image + "=".repeat(paddingCount);
            }
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            emailService.sendEmailWithImage(email, imageBytes);

            return "Email enviado com sucesso para: " + email;
        } catch (Exception e) {
            return "Erro ao enviar email: " + e.getMessage();
        }
    }
}