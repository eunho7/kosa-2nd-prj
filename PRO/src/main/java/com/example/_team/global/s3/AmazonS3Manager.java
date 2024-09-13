package com.example._team.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example._team.config.AmazonConfig;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {
    private final AmazonS3 amazonS3;
    private final AmazonConfig amazonConfig;

    public String uploadFile(String dirName, MultipartFile file) {
        String keyName = generateReviewKeyName(UUID.randomUUID(), dirName);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
        } catch (IOException e) {
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public String generateReviewKeyName(UUID uuid, String dirName) {
        return dirName + '/' + uuid;
    }
}
