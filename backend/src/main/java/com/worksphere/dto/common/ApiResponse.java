package com.worksphere.dto.common;

public record ApiResponse(boolean success, String message) {

    public static ApiResponse ok(String m) {
        return new ApiResponse(true, m);
    }
}
