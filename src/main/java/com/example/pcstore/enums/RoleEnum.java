package com.example.pcstore.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    NON("NON"),
    ALL("ALL");

    private final String value;
}
