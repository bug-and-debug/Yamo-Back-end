package com.locassa.yamo.service.aws;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locassa.yamo.util.YamoUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;

public class AwsS3Service {

    private static final Logger logger = Logger.getLogger(AwsS3Service.class);

    private AmazonS3Client s3Client;
    private String bucketName;

    public AwsS3Service(String key, String secret, String endpoint, String bucketName) {
        this.bucketName = bucketName;
        BasicAWSCredentials credentials = new BasicAWSCredentials(key, secret);
        s3Client = new AmazonS3Client(credentials);
        s3Client.setEndpoint(endpoint);
    }

    public String storeFileDataForUser(String base64, String userId) {
        String url = null;
        if (base64 != null && base64.length() > 0) {
            ObjectMapper mapper = new ObjectMapper();
            byte[] encodedData = mapper.convertValue(base64, byte[].class);
            url = storeFileDataForUser(encodedData, String.valueOf(userId));
        }
        return url;
    }

    public String storeFileDataForUser(byte[] encodedData, String userId) {
        String url = null;
        if (encodedData != null && encodedData.length > 0) {
            url = storeFile(
                    encodedData,
                    String.format("profile/user%s-%s.%s",
                            userId,
                            YamoUtils.generateRandomString(),
                            "png"));
        }
        return url;
    }

    private String storeFile(byte[] fileData, String filename) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileData.length);
        metadata.setContentType("image/png");
        PutObjectRequest request = new PutObjectRequest(bucketName, filename, new ByteArrayInputStream(fileData), metadata);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        s3Client.putObject(request);

        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, filename);
    }

}
