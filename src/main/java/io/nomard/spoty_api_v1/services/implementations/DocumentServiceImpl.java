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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final Path uploadPath = Paths.get(System.getProperty("user.home") + "/uploads/");

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
    public List<String> fileList() {
        File dir = new File(uploadPath.toUri().toString());
        File[] files = dir.listFiles();

        return files != null ? Arrays.stream(files).map(File::getName).collect(Collectors.toList()) : null;
    }

    @Override
    public String save(MultipartFile file) {
        var fileName = file.getOriginalFilename();
        var fileCode = RandomStringUtils.randomAlphanumeric(8);
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileCode + '-' + fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/documents/show/image/" + fileCode + '-' + fileName)
                    .toUriString();
        } catch (IOException ioe) {
            throw new RuntimeException("Error whilst saving file: " + fileName, ioe);
        }
    }

    @Override
    public void delete(String fileUrl) throws IOException {
        // Find the last occurrence of '/'
        int lastSlashIndex = fileUrl.lastIndexOf('/');
        // Extract the file name
        String fileName = fileUrl.substring(lastSlashIndex + 1);
        Path path = Paths.get(System.getProperty("user.home") + "/uploads/" + fileName);
        Files.delete(path);
    }

    @Override
    public ResponseEntity<?> download(String fileCode) throws MalformedURLException {
        Path file = Paths.get(System.getProperty("user.home") + "/uploads/" + fileCode);
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
        Path path = Paths.get(System.getProperty("user.home") + "/uploads/" + fileCode);
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
        Path path = Paths.get(System.getProperty("user.home") + "/uploads/" + fileCode);
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
