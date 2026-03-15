package com.example.pcstore.service.impl;

import com.example.pcstore.constant.Constant;
import com.example.pcstore.entity.Category;
import com.example.pcstore.entity.UserProfile;
import com.example.pcstore.exception.BusinessException;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.mapper.CategoriesMapper;
import com.example.pcstore.mapper.UserProfileMapper;
import com.example.pcstore.model.SortOrder;
import com.example.pcstore.model.response.CategoriesResponse;
import com.example.pcstore.model.response.CategoryResponse;
import com.example.pcstore.model.response.UserProfileResponse;
import com.example.pcstore.model.response.UserProfilesResponse;
import com.example.pcstore.repositories.UserProfileRepository;
import com.example.pcstore.repositories.spectification.CategorySpecification;
import com.example.pcstore.service.AdminService;
import com.example.pcstore.service.KeycloakService;
import com.example.pcstore.utils.JWTUtils;
import com.example.pcstore.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Logger LOGGER = LoggingFactory.getLogger(AdminServiceImpl.class);

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final CategorySpecification categorySpecification;
    private final CategoriesMapper categoriesMapper;
    private final KeycloakService keycloakService;
    private final KeycloakSpringBootProperties keycloakSpringBootProperties;

    @Override
    public UserProfilesResponse getUserProfiles(int page, int size, String name, String username, String email) {
        String usernameLogin = JWTUtils.getUsername();

        List<UserProfile> userProfiles = userProfileRepository.findUserProfilesOrderByCreatedAtDesc();
        LOGGER.info("[ADMIN][{}][GET USERS] User Profiles: {}", usernameLogin, userProfiles);

        userProfiles = filterUsersProfile(name, username, email, userProfiles, usernameLogin);

        Keycloak keycloak = keycloakService.getKeycloakByClient();
        UsersResource usersResource = keycloak.realm(keycloakSpringBootProperties.getRealm()).users();
        List<UserProfileResponse> userProfileResponses = new ArrayList<>();
        for (UserProfile userProfile : userProfiles.stream().skip((long) page * size).limit(size).toList()) {
            UserProfileResponse userProfileResponse = userProfileMapper.mapToUserProfileResponse(userProfile);
            usersResource.get(userProfile.getKeycloakId()).roles().getAll();
            userProfileResponses.add(userProfileResponse);
        }

        LOGGER.info("[ADMIN][{}][GET USERS] User Profiles Response: {}", usernameLogin, userProfileResponses);
        return UserProfilesResponse.builder()
                .page(page)
                .size(size)
                .total(userProfiles.size())
                .userProfiles(userProfileResponses)
                .build();
    }

    private static List<UserProfile> filterUsersProfile(String name, String username, String email, List<UserProfile> userProfiles, String usernameLogin) {
        if (StringUtils.isNotNullOrEmpty(name)) {
            userProfiles = userProfiles.stream().filter(item -> getFullName(item).toLowerCase().contains(name.toLowerCase())).toList();
            LOGGER.info("[ADMIN][{}][GET USERS] Danh sách user tìm kiếm theo name: {}", usernameLogin, userProfiles);
        }

        if (StringUtils.isNotNullOrEmpty(username)) {
            userProfiles = userProfiles.stream().filter(item -> item.getUsername().toLowerCase().contains(username.toLowerCase())).toList();
            LOGGER.info("[ADMIN][{}][GET USERS] Danh sách user tìm kiếm theo username: {}", usernameLogin, userProfiles);
        }

        if (StringUtils.isNotNullOrEmpty(email)) {
            userProfiles = userProfiles.stream().filter(item -> item.getEmail().toLowerCase().contains(email.toLowerCase())).toList();
            LOGGER.info("[ADMIN][{}][GET USERS] Danh sách user tìm kiếm theo email: {}", usernameLogin, userProfiles);
        }
        return userProfiles;
    }

    private static String getFullName(UserProfile item) {
        return item.getFirstName() + " " + item.getLastName();
    }

    @Override
    public UserProfileResponse getUserByKeycloakId(String keycloakId) {
        String username = JWTUtils.getUsername();
        LOGGER.info("[ADMIN][{}][GET USER] Keycloak ID: {}", username, keycloakId);

        Optional<UserProfile> userProfileOptional = userProfileRepository.findByKeycloakId(keycloakId);
        if (userProfileOptional.isEmpty()) {
            LOGGER.error("[ADMIN][{}][GET USER] User không tồn tại trong hệ thống", username);
            throw new BusinessException(Constant.USER_NOT_EXIST);
        }

        UserProfile userProfile = userProfileOptional.get();
        LOGGER.info("[ADMIN][{}][GET USER] User Profile: {}", username, userProfile);

        return userProfileMapper.mapToUserProfileResponse(userProfile);
    }

    @Override
    public CategoriesResponse getCategories(int page, int size, String name, Boolean status, List<String> orderBy) {
        List<Category> categories = categorySpecification.findAllByStatus(status);

        if (CollectionUtils.isEmpty(categories)) {
            return new CategoriesResponse(0, Collections.emptyList());
        }

        categories = filterCategoryByName(name, categories);
        int total = categories.size();
        categories = paginationCategory(page, size, categories);
        List<CategoryResponse> categoriesResponses = categories.stream().map(categoriesMapper::mapToCategoryResponse).toList();
        categoriesResponses = sortCategories(orderBy, categoriesResponses);
        return new CategoriesResponse(total, categoriesResponses);
    }

    private List<SortOrder> parseSortOrders(List<String> orderBy) {
        if (orderBy == null) {
            return Collections.emptyList();
        }
        return orderBy.stream()
                .map(order -> {
                    String[] parts = order.split(":");
                    String field = parts[0];
                    String direction = parts.length > 1 ? parts[1] : "ASC";
                    return new SortOrder(field, direction);
                })
                .toList();
    }

    private List<CategoryResponse> sortCategories(List<String> orderBy, List<CategoryResponse> categoriesResponses) {
        List<SortOrder> sortOrders = parseSortOrders(orderBy);
        if (sortOrders.isEmpty()) {
            return categoriesResponses;
        }

        Comparator<CategoryResponse> comparator = Comparator.comparingInt(o -> 0);

        for (SortOrder sortOrder : sortOrders) {
            Comparator<CategoryResponse> fieldComparator;
            switch (sortOrder.getField()) {
                case "active":
                    fieldComparator = Comparator.comparing(CategoryResponse::isActive);
                    break;
                case "name":
                    Collator collator = Collator.getInstance(new Locale("vi", "VN"));
                    fieldComparator = Comparator.comparing(CategoryResponse::getName, collator);
                    break;
                default:
                    continue;
            }

            if ("desc".equalsIgnoreCase(sortOrder.getDirection())) {
                fieldComparator = fieldComparator.reversed();
            }

            comparator = comparator.thenComparing(fieldComparator);
        }

        return categoriesResponses.stream().sorted(comparator).toList();
    }

    private static List<Category> paginationCategory(long page, int size, List<Category> categories) {
        return categories.stream().skip(page * size).limit(size).toList();
    }

    private static List<Category> filterCategoryByName(String name, List<Category> categories) {
        if (StringUtils.isNotNullOrEmpty(name)) {
            categories = categories.stream().filter(item -> item.getName().toLowerCase().contains(name.toLowerCase())).toList();
        }
        return categories;
    }

}

