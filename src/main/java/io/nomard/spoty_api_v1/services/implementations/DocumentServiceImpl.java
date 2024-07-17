package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.services.interfaces.DocumentService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
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
    public Flux<?> fileList() {
        return Mono.fromCallable(() -> {
            File dir = new File(uploadPath.toUri());
            File[] files = dir.listFiles();
            return files != null ? Arrays.stream(files).map(File::getName).collect(Collectors.toList()) : List.of();
        }).flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<String> save(MultipartFile file) {
        return Mono.fromCallable(() -> {
            var fileName = file.getOriginalFilename();
            var fileCode = RandomStringUtils.randomAlphanumeric(8);
            try (InputStream inputStream = file.getInputStream()) {
                Path filePath = uploadPath.resolve(fileCode + '-' + fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                return "/documents/show/image/" + fileName;
                //return ServletUriComponentsBuilder.fromCurrentContextPath()
                //        .path("/documents/show/image/" + fileName)
                //        .toUriString();
            }
        }).onErrorMap(throwable -> new RuntimeException("Error whilst saving file: " + file.getOriginalFilename(), throwable));
    }

//    @Override
//    public Mono<String> save(MultipartFile file) {
//        return Mono.fromCallable(() -> {
//                    var fileName = file.getOriginalFilename();
//                    var fileCode = RandomStringUtils.randomAlphanumeric(8);
//                    Path filePath = uploadPath.resolve(fileCode + '-' + fileName);
//                    return DataBufferUtils.readInputStream(file::getInputStream, Schedulers.boundedElastic(), 4096)
//                            .flatMap(dataBuffer -> DataBufferUtils.write(Flux.just(dataBuffer), filePath, StandardOpenOption.CREATE)
//                                    .then(Mono.just(dataBuffer)))
//                            .then()
//                            .thenReturn("/documents/show/image/" + fileCode + '-' + fileName);
//                })
//                .flatMap(mono -> mono)
//                .onErrorMap(throwable -> new RuntimeException("Error whilst saving file: " + file.getOriginalFilename(), throwable));
//    }

    @Override
    public Mono<ResponseEntity<?>> download(String fileCode) {
        return Mono.fromCallable(() -> {
            Path file = findFileByCode(fileCode);
            if (file != null) {
                Resource resource = new UrlResource(file.toUri());
                String contentType = "application/octet-stream";
                String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        });
    }

    @Override
    public Mono<ResponseEntity<Resource>> showImage(String fileCode) {
        return Mono.fromCallable(() -> {
            Path path = findFileByCode(fileCode);
            if (path != null) {
                Resource resource = new UrlResource(path.toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        });
    }

    @Override
    public Mono<ResponseEntity<Resource>> showFile(String fileCode) {
        return Mono.fromCallable(() -> {
            Path path = findFileByCode(fileCode);
            if (path != null) {
                Resource resource = new UrlResource(path.toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        });
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
