package com.server.oceankeeper.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class MultipartToImgUtil {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Optional<File> multipartUpload(MultipartFile multipartFile) throws IOException {
        return convertMultipartFileToFile(multipartFile);
    }

    //TODO: S3 업로드를 꼭 파일을 통해서 해야하는가?
    private Optional<File> convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());

        if (file.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(file);
        } else {
            log.error("파일 생성 실패 file:{}", file.getName());
        }

        return Optional.empty();
    }

    protected boolean removeFile(File file) {
        return file.delete();
    }

    String randomFileName(File file, String dirName) {
        return dirName + "/" + UUID.randomUUID() + file.getName();
    }
}
