package pjserrano.common.model;

import lombok.Getter;

@Getter
public enum RoleType {
    ROLE_USER("USER"),
    ROLE_OPERATOR("OPERATOR"),
    ROLE_ADMIN("ADMIN");

    private final String name;

    RoleType(String name) { this.name = name; }
}
