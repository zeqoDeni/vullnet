package org.vullnet.vullnet00.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final org.springframework.core.env.Environment environment;

    public String storeAvatar(Long userId, MultipartFile file) {
        try {
            validateImage(file);
            String uploadDir = environment.getProperty("app.upload.dir", "/tmp/vullnet/uploads");
            Path baseDir = Paths.get(uploadDir, "avatars");
            Files.createDirectories(baseDir);
            String ext = getExtension(file.getOriginalFilename());
            String filename = userId + "-" + UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
            Path target = baseDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/avatars/")
                    .path(filename)
                    .toUriString();
        } catch (IOException e) {
            throw new RuntimeException("Dështoi ruajtja e skedarit", e);
        }
    }

    public String storeBlogImage(Long userId, MultipartFile file) {
        try {
            validateImage(file);
            String uploadDir = environment.getProperty("app.upload.dir", "/tmp/vullnet/uploads");
            Path baseDir = Paths.get(uploadDir, "blog");
            Files.createDirectories(baseDir);
            String ext = getExtension(file.getOriginalFilename());
            String filename = (userId != null ? userId : 0) + "-" + UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
            Path target = baseDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/blog/")
                    .path(filename)
                    .toUriString();
        } catch (IOException e) {
            throw new RuntimeException("Dështoi ruajtja e skedarit", e);
        }
    }

    public String storeRequestImage(Long userId, MultipartFile file) {
        try {
            validateImage(file);
            String uploadDir = environment.getProperty("app.upload.dir", "/tmp/vullnet/uploads");
            Path baseDir = Paths.get(uploadDir, "requests");
            Files.createDirectories(baseDir);
            String ext = getExtension(file.getOriginalFilename());
            String filename = (userId != null ? userId : 0) + "-" + UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
            Path target = baseDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/requests/")
                    .path(filename)
                    .toUriString();
        } catch (IOException e) {
            throw new RuntimeException("Dështoi ruajtja e skedarit", e);
        }
    }

    private String getExtension(String name) {
        if (name == null) return "";
        int idx = name.lastIndexOf('.');
        if (idx == -1) return "";
        return name.substring(idx + 1);
    }

    private void validateImage(MultipartFile file) throws IOException {
        long maxBytes = environment.getProperty("app.upload.max-bytes", Long.class, 10 * 1024 * 1024L);
        if (file.getSize() > maxBytes) {
            throw new IOException("Skedari është shumë i madh");
        }
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/"))) {
            throw new IOException("Lejohen vetëm imazhe");
        }
    }
}
