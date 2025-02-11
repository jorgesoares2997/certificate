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
            helper.setSubject("🎓 Parabéns! Seu Certificado de Conclusão Está Aqui");
            String emailContent = """
                        <p>Olá,</p>
                        <p>Parabéns por concluir um curso na <strong>NearX Educational Platform</strong>! 🎉</p>
                        <p>Seu esforço e dedicação são inspiradores, e estamos muito felizes em fazer parte da sua jornada de aprendizado. Como reconhecimento pelo seu empenho, anexamos o seu certificado de conclusão a este e-mail.</p>
                        <p>📜 <strong>O que fazer agora?</strong></p>
                        <ul>
                            <li>Baixe o certificado e guarde-o com carinho.</li>
                            <li>Compartilhe sua conquista no LinkedIn e em outras redes sociais!</li>
                            <li>Continue aprendendo com a NearX e explore novos cursos em nossa plataforma.</li>
                        </ul>
                        <p>Se precisar de qualquer suporte, estamos à disposição.</p>
                        <p><em>Desejamos muito sucesso na sua trajetória! 🚀</em></p>
                        <p>Atenciosamente,</p>
                        <p><strong>Equipe NearX Educational Platform</strong></p>
                        <p>📧 suporte@nearx.com</p>
                    """
                    .formatted();

            helper.setText(emailContent, true);

            InputStreamSource imageSource = new ByteArrayResource(imageBytes);
            helper.addAttachment("certificate.png", imageSource, "image/png");

            mailSender.send(message);
            LOGGER.info("Email enviado com sucesso para: " + to);

        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Erro ao enviar email para: " + to, e);
        }
    }
}