package com.example.pcstore.service;

import com.example.pcstore.model.response.CategoriesResponse;
import com.example.pcstore.model.response.UserProfileResponse;
import com.example.pcstore.model.response.UserProfilesResponse;

import java.util.List;

public interface AdminService {

    UserProfilesResponse getUserProfiles(int page, int size, String name, String username, String email);

    UserProfileResponse getUserByKeycloakId(String keycloakId);

    CategoriesResponse getCategories(int page, int size, String name, Boolean status, List<String> orderBy);
}

