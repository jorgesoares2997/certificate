package com.nearx.image_overlay.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nearx.image_overlay.model.Certificate;
import com.nearx.image_overlay.repository.CertificateRepository;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    public Certificate uploadCertificate(String fileName, String imageUrl) {
        Certificate certificate = new Certificate(fileName, imageUrl);
        return certificateRepository.save(certificate);
    }

    public Optional<Certificate> getCertificateById(Long certificateId) {
        return certificateRepository.findById(certificateId);
    }
}