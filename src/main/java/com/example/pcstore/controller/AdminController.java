package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.model.response.CategoriesResponse;
import com.example.pcstore.model.response.UserProfileResponse;
import com.example.pcstore.model.response.UserProfilesResponse;
import com.example.pcstore.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin Controller", description = "Danh sách API của Administration")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Lấy danh sách người dùng")
    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/users")
    public ResponseEntity<UserProfilesResponse> getUsers(@RequestParam(required = false) int page,
                                                         @RequestParam(required = false) int size,
                                                         @RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String username,
                                                         @RequestParam(required = false) String email) {
        return ResponseEntity.ok(adminService.getUserProfiles(page, size, name, username, email));
    }

    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/user/{keycloakId}")
    public ResponseEntity<UserProfileResponse> getUserByKeycloakId(@PathVariable(name = "keycloakId") String keycloakId) {
        return ResponseEntity.ok(adminService.getUserByKeycloakId(keycloakId));
    }

    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/categories")
    public ResponseEntity<CategoriesResponse> getCategories(@RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestParam(required = false) String name,
                                                            @RequestParam(required = false) Boolean status,
                                                            @RequestParam(name = "sort", required = false) List<String> orderBy) {
        return ResponseEntity.ok(adminService.getCategories(page, size, name, status, orderBy));
    }

}


