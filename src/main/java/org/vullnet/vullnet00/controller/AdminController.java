package org.vullnet.vullnet00.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vullnet.vullnet00.dto.AdminStatsResponse;
import org.vullnet.vullnet00.security.UserPrincipal;
import org.vullnet.vullnet00.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public AdminStatsResponse stats(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null || principal.getAuthorities().stream().noneMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            throw new AccessDeniedException("E ndaluar");
        }
        return adminService.getStats();
    }
}
