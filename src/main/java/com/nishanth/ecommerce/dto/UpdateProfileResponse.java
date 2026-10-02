package com.nishanth.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProfileResponse {

    private UserProfileResponse user;
    private String token;
}