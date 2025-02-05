package com.sponsorando.app.services;

import com.sponsorando.app.models.Image;
import com.sponsorando.app.models.ImageForm;
import com.sponsorando.app.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public String createImage(ImageForm form) {
//    public Image createImage(ImageForm form) {
//        Image image = new Image();

        String extension = "";

        try {

            int dotIndex = form.getUrl().getOriginalFilename().lastIndexOf(".");

            if (dotIndex >= 0) {
                extension = "." + form.getUrl().getOriginalFilename().substring(dotIndex + 1);
            }

            String filename = UUID.randomUUID().toString();

            Path appRoot = Path.of("").toAbsolutePath(); // gets the current folder
            Path uploadsPath = appRoot.resolve("src/main/resources/static/uploads");
            Path destination = uploadsPath.resolve(filename);
            Resource staticResource = new ClassPathResource("static");
            Path targetUploadsPath = Path.of(staticResource.getURI()).resolve("uploads");

            if(Files.notExists(targetUploadsPath)) {
                Files.createDirectory(targetUploadsPath);
            }

            Path targetDestination = targetUploadsPath.resolve(filename);

            form.getUrl().transferTo(destination);
            form.getUrl().transferTo(targetDestination);
//
//            image.setUrl(filename);
//            image.setAltText(LocalDateTime.now()+ "_" + form.getUrl().getOriginalFilename());
//
//            return image;
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public boolean deleteByCampaignId(Long campaignId) {
        try {
            imageRepository.deleteByCampaignId(campaignId);
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }
}
