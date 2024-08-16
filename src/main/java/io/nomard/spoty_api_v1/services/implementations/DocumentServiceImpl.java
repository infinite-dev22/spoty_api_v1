package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.services.interfaces.DocumentService;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log
public class DocumentServiceImpl implements DocumentService {

    @Value("${app.upload-dir}")
    private String uploadDir;
    private Path productImagesUploadPath;

    @PostConstruct
    public void init() {
        productImagesUploadPath = Paths.get(uploadDir + "/images/products");
        try {
            Files.createDirectories(productImagesUploadPath);
            log.log(Level.INFO, "Upload directory created/exists at {}", productImagesUploadPath.toString());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not initialize storage: {}", e.getMessage());
            throw new IllegalStateException("Could not initialize storage", e);
        }
    }

    @Override
    public List<String> fileList() {
        try (Stream<Path> files = Files.list(productImagesUploadPath)) {
            return files.map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error listing files: {}", e.getMessage());
            throw new RuntimeException("Error listing files", e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileCode = RandomStringUtils.randomAlphanumeric(8);
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = productImagesUploadPath.resolve(fileCode + '-' + fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.log(Level.INFO, "File saved successfully at {}", filePath.toString());
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/documents/show/image/")
                    .path(fileCode + '-' + fileName)
                    .toUriString();
        } catch (IOException ioe) {
            log.log(Level.SEVERE, "Error saving file {}", fileName + ": " + ioe.getMessage());
            throw new RuntimeException("Error whilst saving file: " + fileName, ioe);
        }
    }

    @Override
    public void delete(String fileUrl) throws IOException {
        String fileName = getFileNameFromUrl(fileUrl);
        Path path = productImagesUploadPath.resolve(fileName);
        Files.deleteIfExists(path);
        log.log(Level.INFO, "File {} deleted successfully", fileName);
    }

    @Override
    public ResponseEntity<?> download(String fileCode) {
        Optional<Path> fileOptional = findFileByCode(fileCode);
        if (fileOptional.isPresent()) {
            try {
                Path file = fileOptional.get();
                Resource resource = new UrlResource(file.toUri());
                String contentType = Files.probeContentType(file);
                contentType = contentType != null ? contentType : "application/octet-stream";
                String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error downloading file {}", fileCode + ": " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error while downloading file");
            }
        }
        log.log(Level.WARNING, "File {} not found", fileCode);
        return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Resource> showImage(String fileCode) {
        return serveFile(fileCode, MediaType.IMAGE_JPEG);
    }

    @Override
    public ResponseEntity<Resource> showFile(String fileCode) {
        return serveFile(fileCode, MediaType.APPLICATION_PDF);
    }

    private ResponseEntity<Resource> serveFile(String fileCode, MediaType mediaType) {
        Optional<Path> fileOptional = findFileByCode(fileCode);
        if (fileOptional.isPresent()) {
            try {
                Resource resource = new UrlResource(fileOptional.get().toUri());
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .body(resource);
            } catch (MalformedURLException e) {
                log.log(Level.SEVERE, "Error serving file {}", fileCode + ": " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null);
            }
        }
        log.log(Level.WARNING, "File {} not found", fileCode);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private Optional<Path> findFileByCode(String fileCode) {
        try (Stream<Path> files = Files.list(productImagesUploadPath)) {
            return files.filter(file -> file.getFileName().toString().startsWith(fileCode))
                    .findFirst();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error searching file by code {}", fileCode + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    private String getFileNameFromUrl(String fileUrl) {
        return Paths.get(fileUrl).getFileName().toString();
    }
}
