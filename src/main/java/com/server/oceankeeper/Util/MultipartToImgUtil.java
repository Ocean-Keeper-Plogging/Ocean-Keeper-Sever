package com.server.oceankeeper.Util;

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

        Optional<File> file = convertMultipartFileToFile(multipartFile);

        return file;
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

    protected void removeFile(File file) {
        file.delete();
    }

    String randomFileName(File file, String dirName) {
        return dirName + "/" + UUID.randomUUID() + file.getName();
    }
}
