package com.devspark.childcare.child;

import com.devspark.childcare.child.dto.ChildRegistrationDto;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/child")
@RequiredArgsConstructor
public class ChildController {

    private final ChildRepository childRepository;

    @PostMapping("/register")
    public ApiResponse<String> registerChild(@RequestBody ChildRegistrationDto dto) {
        String[] nameParts = dto.getFullName().split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        Child child = Child.builder()
                .firstName(firstName)
                .lastName(lastName)
                .guardianEmail(dto.getParentEmail())
                .guardianName(dto.getParentFullName())
                .dob(dto.getDob())
                .gender(Child.Gender.valueOf(dto.getGender().toUpperCase()))
                .bloodGroup(dto.getBloodGroup())
                .weight(dto.getWeight())
                .height(dto.getHeight())
                .specialNote(dto.getSpecialNote())
                .build();

        childRepository.save(child);
        return ApiResponse.success("Child successfully registered in the system", null);
    }

    @GetMapping("/all")
    public ApiResponse<java.util.List<Child>> getAllChildren() {
        return ApiResponse.success("Fetched all children", childRepository.findAll());
    }
}
