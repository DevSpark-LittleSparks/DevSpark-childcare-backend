package com.devspark.childcare.settings;

import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    @GetMapping
    public ApiResponse<UserSettingsDto> getSettings(Principal principal) {
        if (principal == null) {
            return ApiResponse.error("Unauthorized");
        }
        return ApiResponse.success("Settings fetched successfully", userSettingsService.getSettings(principal.getName()));
    }

    @PutMapping
    public ApiResponse<UserSettingsDto> updateSettings(Principal principal, @RequestBody UserSettingsDto dto) {
        if (principal == null) {
            return ApiResponse.error("Unauthorized");
        }
        return ApiResponse.success("Settings updated successfully", userSettingsService.updateSettings(principal.getName(), dto));
    }
}
