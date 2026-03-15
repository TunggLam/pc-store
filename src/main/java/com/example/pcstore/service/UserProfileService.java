package com.example.pcstore.service;

import com.example.pcstore.entity.UserProfile;
import com.example.pcstore.model.response.UserProfileResponse;

public interface UserProfileService {

    UserProfile findUserProfileByUsername(String username);

    UserProfileResponse myProfile();

    String getFullNameByUsername(String username);
}

