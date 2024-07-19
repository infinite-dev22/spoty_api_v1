package io.nomard.spoty_api_v1.services.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    List<String> fileList();

    String save(MultipartFile file) throws IOException;

    ResponseEntity<?> download(String fileCode) throws IOException;

    ResponseEntity<Resource> showImage(String fileCode) throws IOException;

    ResponseEntity<Resource> showFile(String fileCode) throws IOException;
}
