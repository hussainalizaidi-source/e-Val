package com.e_val.e_Val.model.enums;

public enum Role {
    ADMIN("ROLE_ADMIN"),
    TEACHER("ROLE_TEACHER"), 
    STUDENT("ROLE_STUDENT");
    
    private final String authority;
    
    Role(String authority) {
        this.authority = authority;
    }
    
    public String getAuthority() {
        return authority;
    }
    
    // Add this method to convert from String to Role
    public static Role fromString(String text) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}