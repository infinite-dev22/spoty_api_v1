package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.services.interfaces.DocumentService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final Path uploadPath = Paths.get(System.getProperty("user.home") + "/uploads/");
    private final ConcurrentHashMap<String, Path> fileCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        var fileName = file.getOriginalFilename();
        var fileCode = RandomStringUtils.randomAlphanumeric(8);
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileCode + '-' + fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            fileCache.put(fileCode, filePath);
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path('/' + fileCode)
                    .toUriString();
        } catch (IOException ioe) {
            throw new RuntimeException("Error whilst saving file: " + fileName, ioe);
        }
    }

    @Override
    public ResponseEntity<?> download(String fileCode) throws MalformedURLException {
        Path file = fileCache.computeIfAbsent(fileCode, this::findFileByCode);
        if (file != null) {
            var resource = new UrlResource(file.toUri());
            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
        }
        return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Resource> showImage(String fileCode) throws IOException {
        // Path to the image file
        Path path = fileCache.computeIfAbsent(fileCode, this::findFileByCode);
        // Load the resource
        Resource resource = new UrlResource(path.toUri());
        // Return ResponseEntity with image content type
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @Override
    public ResponseEntity<Resource> showFile(String fileCode) throws IOException {
        // Path to the PDF file
        Path path = fileCache.computeIfAbsent(fileCode, this::findFileByCode);
        // Load the resource
        Resource resource = new UrlResource(path.toUri());
        // Return ResponseEntity with PDF content type
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    private Path findFileByCode(String fileCode) {
        try (Stream<Path> files = Files.list(uploadPath)) {
            return files
                    .filter(file -> file.getFileName().toString().startsWith(fileCode))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            throw new RuntimeException("Error searching file by code: " + fileCode, e);
        }
    }
}
