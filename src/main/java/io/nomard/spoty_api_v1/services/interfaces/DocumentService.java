package io.nomard.spoty_api_v1.services.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface DocumentService {
    Flux<?> fileList();

    Mono<String> save(MultipartFile file) throws IOException;

    Mono<ResponseEntity<?>> download(String fileCode) throws IOException;

    Mono<ResponseEntity<Resource>> showImage(String fileCode) throws IOException;

    Mono<ResponseEntity<Resource>> showFile(String fileCode) throws IOException;
}
