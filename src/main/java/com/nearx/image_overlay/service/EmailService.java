package com.nearx.image_overlay.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailWithImage(String to, byte[] imageBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setFrom("jorgesoares2997@gmail.com"); 
            helper.setSubject("Sua imagem gerada");
            helper.setText("Olá, segue em anexo a imagem gerada!");

            // Criando o anexo da imagem
            InputStreamSource imageSource = new ByteArrayResource(imageBytes);
            helper.addAttachment("image.png", imageSource, "image/png");

            mailSender.send(message);
            LOGGER.info("Email enviado com sucesso para: " + to);

        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Erro ao enviar email para: " + to, e);
        }
    }
}