package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.errors.StorageException;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log
public class DocumentServiceImpl implements DocumentService {

    @Value("${app.upload-dir}")
    private String rootLocation;
    private Path productImagesLocation;
    private Path userImagesLocation;
    private Path documentLocation;

    @PostConstruct
    public void init() {
        createDirectories();
    }

    private void createDirectories() {
        productImagesLocation = Paths.get(rootLocation, "images", "products");
        userImagesLocation = Paths.get(rootLocation, "images", "users");
        documentLocation = Paths.get(rootLocation, "images", "documents");

        createDirectory(productImagesLocation, "Product images");
        createDirectory(userImagesLocation, "User images");
        createDirectory(documentLocation, "Documents");
    }

    private void createDirectory(Path path, String dirType) {
        try {
            Files.createDirectories(path);
            log.log(Level.INFO, "{0} upload directory created/exists at {1}", new Object[]{dirType, path.toString()});
        } catch (IOException e) {
            handleInitException(dirType, e);
        }
    }

    private void handleInitException(String dirType, IOException e) {
        log.log(Level.SEVERE, "Could not initialize " + dirType + " storage: {}", e.getMessage());
        throw new StorageException("Could not initialize " + dirType + " storage", e);
    }

    @Override
    public HashSet<Path> filePathStream(String filename) {
        try (Stream<Path> filePathsStream = Files.find(Path.of(rootLocation), 5, (path, basicFileAttributes) -> basicFileAttributes.isRegularFile())) {
            return (HashSet<Path>) filePathsStream.filter(s -> s.toString().contains(filename)).collect(Collectors.toSet());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error listing files: {}", e.getMessage());
            throw new StorageException("Error listing files", e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileCode = RandomStringUtils.randomAlphanumeric(8);
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = productImagesLocation.resolve(fileCode + '-' + fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.log(Level.INFO, "File saved successfully at {}", filePath.toString());
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/products/show/")
                    .path(fileCode + '-' + fileName)
                    .toUriString();
        } catch (IOException ioe) {
            log.log(Level.SEVERE, "Error saving file {}", fileName + ": " + ioe.getMessage());
            throw new StorageException("Error whilst saving file: " + fileName, ioe);
        }
    }

    @Override
    public void delete(String fileUrl) {
        this.filePathStream(getFileNameFromUrl(fileUrl)).forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.log(Level.INFO, "File {} deleted successfully", path.getFileName());
        });
    }

    @Override
    public ResponseEntity<?> download(String fileCode) {
        return findFileByCode(fileCode).map(path -> {
            try {
                Resource resource = new UrlResource(path.toUri());
                String contentType = Files.probeContentType(path);
                contentType = contentType != null ? contentType : "application/octet-stream";
                String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            } catch (MalformedURLException e) {
                log.log(Level.SEVERE, "Error downloading file {0}: {1}", new Object[]{path.getFileName(), e.getMessage()});
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error while downloading file");
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error getting file {0}: {1}", new Object[]{path.getFileName(), e.getMessage()});
                return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
            }
        }).orElseGet(() -> {
            log.log(Level.WARNING, "File {} not found", fileCode);
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        });

    }

    @Override
    public ResponseEntity<?> showImage(String fileCode) {
        return serveFile(fileCode, MediaType.IMAGE_JPEG);
    }

    @Override
    public ResponseEntity<?> showFile(String fileCode) {
        return serveFile(fileCode, MediaType.APPLICATION_PDF);
    }

    private ResponseEntity<?> serveFile(String fileCode, MediaType mediaType) {
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
                        .body(e.getLocalizedMessage());
            }
        }
        log.log(Level.WARNING, "File " + fileCode + " not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File " + fileCode + " not found");
    }

    private Optional<Path> findFileByCode(String fileCode) {
        return this.filePathStream(fileCode).stream().findAny();
    }

    private String getFileNameFromUrl(String fileUrl) {
        return Paths.get(fileUrl).getFileName().toString();
    }
}
