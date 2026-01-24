package org.vullnet.vullnet00.controller;

import org.vullnet.vullnet00.dto.UserCreateRequest;
import org.vullnet.vullnet00.dto.UserUpdateRequest;
import org.vullnet.vullnet00.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vullnet.vullnet00.dto.HelpRequestResponse;
import org.vullnet.vullnet00.dto.RoleUpdateRequest;
import org.vullnet.vullnet00.dto.UserProfileResponse;
import org.vullnet.vullnet00.dto.UserProfileUpdateRequest;
import org.vullnet.vullnet00.dto.UserStatusUpdateRequest;
import org.vullnet.vullnet00.service.HelpRequestService;
import org.vullnet.vullnet00.service.UserService;
import org.vullnet.vullnet00.security.UserPrincipal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final HelpRequestService helpRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserCreateRequest req
    ) {
        enforceAdmin(principal);
        return userService.create(req);
    }

    @GetMapping
    public List<UserResponse> getAll(@AuthenticationPrincipal UserPrincipal principal) {
        enforceAdmin(principal);
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponse getById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        enforceSelfOrAdmin(principal, id);
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    public UserResponse update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest req
    ) {
        enforceSelfOrAdmin(principal, id);
        return userService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        enforceSelfOrAdmin(principal, id);
        userService.delete(id);
    }

    @GetMapping("/{id}/requests")
    public Page<HelpRequestResponse> getRequestsByOwner(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            Pageable pageable
    ) {
        enforceSelfOrAdmin(principal, id);
        return helpRequestService.getByOwnerId(id, pageable);
    }

    @GetMapping("/{id}/profile")
    public UserProfileResponse getProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        UserProfileResponse profile = userService.getProfile(id);
        if (!profile.isProfilePublic()) {
            enforceSelfOrAdmin(principal, id);
        }
        return profile;
    }

    @PutMapping("/{id}/profile")
    public UserProfileResponse updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        enforceSelfOrAdmin(principal, id);
        return userService.updateProfile(id, request);
    }

    @PutMapping("/{id}/role")
    public UserResponse updateRole(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request
    ) {
        enforceAdmin(principal);
        org.vullnet.vullnet00.model.Role role = org.vullnet.vullnet00.model.Role.valueOf(request.getRole().toUpperCase());
        return userService.updateRole(id, role);
    }

    @PutMapping("/{id}/status")
    public UserResponse updateStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        enforceAdmin(principal);
        return userService.updateStatus(id, request.getActive());
    }

    private void enforceSelfOrAdmin(UserPrincipal principal, Long userId) {
        if (principal == null) {
            throw new AccessDeniedException("Unauthorized");
        }
        if (principal.getId().equals(userId)) {
            return;
        }
        if (principal.getAuthorities().stream().anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()))) {
            return;
        }
        throw new AccessDeniedException("Forbidden");
    }

    private void enforceAdmin(UserPrincipal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Unauthorized");
        }
        if (principal.getAuthorities().stream().anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()))) {
            return;
        }
        throw new AccessDeniedException("Forbidden");
    }
}
