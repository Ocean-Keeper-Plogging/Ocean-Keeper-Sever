package com.server.oceankeeper.Util;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.File;

@Service
@RequiredArgsConstructor
public class S3Util extends MultipartToImgUtil{


    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    public String uploadS3(File file, String dirName){
        String fileFullPath = randomFileName(file, dirName);
        String url = putToS3(file, fileFullPath);
        removeFile(file);

        return url;
    }


    private String putToS3(File uploadFile, String fileName){
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return getFromS3(bucket, fileName);
    }

    private String getFromS3(String bucket, String fileName){
        return amazonS3.getUrl(bucket, fileName).toString();
    }


    public void deleteFromS3(String fileFullPath){
        if(!amazonS3.doesObjectExist(bucket, fileFullPath)){
            throw new AmazonS3Exception(fileFullPath + "이 존재하지 않습니다.");
        }

        amazonS3.deleteObject(bucket, fileFullPath);

    }



}
