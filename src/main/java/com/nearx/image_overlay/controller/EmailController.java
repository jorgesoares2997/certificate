package com.nearx.image_overlay.controller;

import com.nearx.image_overlay.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public String sendEmail(@RequestParam String email, @RequestBody String base64Image) {
        try {
            // Converte a imagem de Base64 para um array de bytes
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Envia o email com a imagem
            emailService.sendEmailWithImage(email, imageBytes);

            return "Email enviado com sucesso para: " + email;
        } catch (Exception e) {
            return "Erro ao enviar email: " + e.getMessage();
        }
    }
}