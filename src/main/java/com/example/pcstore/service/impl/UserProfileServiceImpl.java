package com.example.pcstore.service.impl;

import com.example.pcstore.entity.UserProfile;
import com.example.pcstore.mapper.UserProfileMapper;
import com.example.pcstore.model.response.UserProfileResponse;
import com.example.pcstore.repositories.UserProfileRepository;
import com.example.pcstore.service.UserProfileService;
import com.example.pcstore.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfile findUserProfileByUsername(String username) {
        return userProfileRepository.findByUsername(username).orElse(null);
    }

    @Override
    public UserProfileResponse myProfile() {
        String username = JWTUtils.getUsername();
        UserProfile userProfile = findUserProfileByUsername(username);
        return userProfileMapper.mapToUserProfileResponse(userProfile);
    }

    @Override
    public String getFullNameByUsername(String username) {
        UserProfile userProfile = findUserProfileByUsername(username);
        if (userProfile == null) {
            return null;
        }
        return userProfile.getFirstName() + " " + userProfile.getLastName();
    }
}

