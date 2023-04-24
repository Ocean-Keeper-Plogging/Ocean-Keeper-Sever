package com.server.oceankeeper.Util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.server.oceankeeper.Exception.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ProfileReqDto multipartUpload(MultipartFile multipartFile, String dirName) throws IOException {
        File file = convertMultipartFileToFile(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File convert fail"));

        return upload(file, dirName);
    }

    public ProfileReqDto upload(File file, String dirName) {
        String fileFullPath = randomFileName(file, dirName);
        String url = putS3(file, fileFullPath);
        removeFile(file);

        return ProfileReqDto
                .builder()
                .fileFullPath(fileFullPath)
                .url(url)
                .build();
    }



    private String randomFileName(File file, String dirName) {
        return dirName + "/" + UUID.randomUUID() + file.getName();
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return getS3(bucket, fileName);
    }

    private String getS3(String bucket, String fileName) {
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private String getFileFullPath(String fileUrl){
        String decodedFileUrl = null;
        try {
            decodedFileUrl = URLDecoder.decode(fileUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CustomApiException("file의 url이 유효하지 않습니다.");
        }
        String[] pathParts = decodedFileUrl.split("/");
        return pathParts[pathParts.length - 2]+pathParts[pathParts.length - 1];
    }

    private void removeFile(File file) {
        file.delete();
    }

    public Optional<File> convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());

        if (file.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(file)){
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(file);
        }
        return Optional.empty();
    }

    public void remove(String fileFullPath) {
        if (!amazonS3.doesObjectExist(bucket, fileFullPath)) {
            throw new AmazonS3Exception("Object " + fileFullPath+ " does not exist!");
        }
        amazonS3.deleteObject(bucket, fileFullPath);


    }


}
