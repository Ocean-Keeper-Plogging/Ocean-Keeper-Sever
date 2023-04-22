package com.server.oceankeeper.Util;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class AwsS3ServiceTest {

    @Autowired
    AwsS3Service awsS3Service;

    @Test
    public void 이미지_업로드() throws Exception{
        //given
        String filePath ="/Users/gimhanju/prj/OceanKeeper/src/main/java/com/server/oceankeeper/upload/test.jpg";
        File targetFile = new File(filePath);
        try{
            AwsS3 ob = awsS3Service.upload(targetFile, "upload");
            System.out.println("성공");
            System.out.println(ob.toString());
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void 이미지_삭제() throws Exception{

        //given
        String filePath ="/Users/gimhanju/prj/OceanKeeper/src/main/java/com/server/oceankeeper/upload/test.jpg";
        File targetFile = new File(filePath);

        AwsS3 ob = awsS3Service.upload(targetFile, "upload");


        try{
            awsS3Service.removeS3File(ob.getFileFullPath());
            System.out.println("성공");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        //when
        //then
    }

}