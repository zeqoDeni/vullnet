package org.vullnet.vullnet00.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.vullnet.vullnet00.security.UserPrincipal;
import org.vullnet.vullnet00.service.FileStorageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/avatar")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> uploadAvatar(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file
    ) {
        String url = fileStorageService.storeAvatar(principal.getId(), file);
        return Map.of("url", url);
    }

    @PostMapping("/blog")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> uploadBlogImage(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file
    ) {
        String url = fileStorageService.storeBlogImage(principal != null ? principal.getId() : null, file);
        return Map.of("url", url);
    }

    @PostMapping("/request")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> uploadRequestImage(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file
    ) {
        String url = fileStorageService.storeRequestImage(principal != null ? principal.getId() : null, file);
        return Map.of("url", url);
    }
}
