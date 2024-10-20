package io.nomard.spoty_api_v1.controllers.norm;

import io.nomard.spoty_api_v1.services.implementations.DocumentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("documents")
public class MultipartController {
    @Autowired
    private DocumentServiceImpl documentService;

    @GetMapping("/download/{fileCode}")
    public ResponseEntity<?> fileDownload(@PathVariable("fileCode") String fileCode) throws MalformedURLException {
        return documentService.download(fileCode);
    }

    @GetMapping("/show/image/{fileCode}")
    public ResponseEntity<?> imageShow(@PathVariable("fileCode") String fileCode) throws IOException {
        return documentService.showImage(fileCode);
    }

    @GetMapping("/show/pdf/{fileCode}")
    public ResponseEntity<?> fileShow(@PathVariable("fileCode") String fileCode) throws IOException {
        return documentService.showFile(fileCode);
    }
}
