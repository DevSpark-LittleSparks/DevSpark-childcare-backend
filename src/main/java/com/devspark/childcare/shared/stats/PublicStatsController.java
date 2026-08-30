package com.devspark.childcare.shared.stats;

import com.devspark.childcare.child.ChildRepository;
import com.devspark.childcare.staff.TeacherRepository;
import com.devspark.childcare.auth.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.devspark.childcare.shared.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicStatsController {

    private final ChildRepository childRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;

    @GetMapping("/landing-stats")
    public ResponseEntity<ApiResponse<StatsDto>> getLandingStats() {
        long childrenEnrolled = childRepository.count();
        long expertStaff = teacherRepository.count();
        long happyFamilies = parentRepository.count();

        // 4th metric is static for now, representing "Years of Excellence" or "Unified Platform"
        long yearsOfExcellence = 10; 

        // If the database is empty or new, provide some base numbers for marketing
        if (childrenEnrolled == 0) childrenEnrolled = 150;
        if (expertStaff == 0) expertStaff = 15;
        if (happyFamilies == 0) happyFamilies = 120;

        StatsDto stats = StatsDto.builder()
                .childrenEnrolled(childrenEnrolled)
                .expertStaff(expertStaff)
                .happyFamilies(happyFamilies)
                .yearsOfExcellence(yearsOfExcellence)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Stats fetched successfully", stats));
    }
}
