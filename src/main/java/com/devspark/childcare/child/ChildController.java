package com.devspark.childcare.child;

import com.devspark.childcare.child.dto.ChildRegistrationDto;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.devspark.childcare.child.dto.ChildResponseDto;

@RestController
@RequestMapping("/api/v1/child")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    @GetMapping("/all")
    public ApiResponse<org.springframework.data.domain.Page<ChildResponseDto>> getAllChildren(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success("Children fetched successfully", childService.getAllChildren(page, size));
    }

    @PostMapping("/register")
    @org.springframework.cache.annotation.CacheEvict(value = "dashboardStats", allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> registerChild(@RequestBody ChildRegistrationDto dto) {
        childService.registerChild(dto);
        return ApiResponse.success("Child registered successfully. Parent can now sign up using the registered email.", null);
    }
}
