package com.example.pcstore.mapper;

import com.example.pcstore.entity.UserProfile;
import com.example.pcstore.model.response.UserProfileResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileResponse mapToUserProfileResponse(UserProfile userProfile);

}

