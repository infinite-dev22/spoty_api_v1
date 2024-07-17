package io.nomard.spoty_api_v1.controllers;

import io.nomard.spoty_api_v1.services.implementations.DocumentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("documents")
public class MultipartController {
    @Autowired
    private DocumentServiceImpl documentService;

    @GetMapping("/download/{fileCode}")
    public Mono<ResponseEntity<?>> fileDownload(@PathVariable("fileCode") String fileCode) throws MalformedURLException {
        return documentService.download(fileCode);
    }

    @GetMapping("/show/image/{fileCode}")
    public Mono<ResponseEntity<Resource>> imageShow(@PathVariable("fileCode") String fileCode) throws IOException {
        return documentService.showImage(fileCode);
    }

    @GetMapping("/show/pdf/{fileCode}")
    public Mono<ResponseEntity<Resource>> fileShow(@PathVariable("fileCode") String fileCode) throws IOException {
        return documentService.showFile(fileCode);
    }
}
