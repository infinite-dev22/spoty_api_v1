package io.nomard.spoty_api_v1.services.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.stream.Stream;

public interface DocumentService {
    HashSet<Path> filePathStream(String filename);

    String save(MultipartFile file) throws IOException;

    void delete(String filePath);

    ResponseEntity<?> download(String fileCode) throws IOException;

    ResponseEntity<?> showImage(String fileCode) throws IOException;

    ResponseEntity<?> showFile(String fileCode) throws IOException;
}
