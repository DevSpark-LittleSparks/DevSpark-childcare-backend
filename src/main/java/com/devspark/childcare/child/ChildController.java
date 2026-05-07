package com.devspark.childcare.child;

import com.devspark.childcare.child.dto.ChildRegistrationDto;
import com.devspark.childcare.shared.response.ApiResponse;
import com.devspark.childcare.auth.Account;
import com.devspark.childcare.auth.AccountRepository;
import com.devspark.childcare.auth.Parent;
import com.devspark.childcare.auth.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/child")
@RequiredArgsConstructor
public class ChildController {

    private final ChildRepository childRepository;
    private final AccountRepository accountRepository;
    private final ParentRepository parentRepository;

    @PostMapping("/register")
    public ApiResponse<String> registerChild(@RequestBody ChildRegistrationDto dto) {
        String[] nameParts = dto.getFullName().split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Age Validation (3-6 years)
        if (dto.getDob() != null) {
            long age = java.time.temporal.ChronoUnit.YEARS.between(dto.getDob(), java.time.LocalDate.now());
            if (age < 3 || age > 6) {
                throw new RuntimeException("Child must be between 3 and 6 years old.");
            }
        }

        // 1. Create Dummy Account if it doesn't exist
        Account account = accountRepository.findByEmail(dto.getParentEmail()).orElse(null);
        if (account == null) {
            account = Account.builder()
                    .email(dto.getParentEmail())
                    .passwordHash("N/A") // Dummy password
                    .role(Account.Role.PARENT)
                    .verified(false)
                    .status(Account.Status.INACTIVE)
                    .build();
            account = accountRepository.save(account);
        }

        // 1.5 Ensure Parent profile exists for this account
        Parent parent = parentRepository.findByAccountAccountId(account.getAccountId()).orElse(null);
        if (parent == null) {
            // Determine relationship from form (default to GUARDIAN)
            Parent.Relationship rel = Parent.Relationship.GUARDIAN;
            if (dto.getRelationship() != null && !dto.getRelationship().isBlank()) {
                try {
                    rel = Parent.Relationship.valueOf(dto.getRelationship().toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }

            parent = Parent.builder()
                    .account(account)
                    .fullName(dto.getParentFullName())
                    .phone(dto.getParentContact())
                    .nic(dto.getParentID())
                    .address(dto.getAddress())
                    .relationship(rel)
                    .build();
            parent = parentRepository.save(parent);
        } else {
            // 1.6 Sibling Detection: Check if this parent already registered a child with
            // same name/DOB
            if (childRepository.existsByFirstNameAndLastNameAndDobAndParentId(firstName, lastName, dto.getDob(),
                    parent.getParentId())) {
                throw new RuntimeException(
                        "This child ( " + dto.getFullName() + " ) is already registered under this parent.");
            }
        }

        // 2. Create the Child record
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
                .address(dto.getAddress())
                .profilePic(dto.getProfilePic())
                .status(Child.Status.ENROLLED) // Default status
                .parentId(parent.getParentId()) // Directly use the parent we found/created
                .build();

        childRepository.save(child);
        return ApiResponse.success("Child and parent profile successfully registered.", null);
    }

    @GetMapping("/all")
    public ApiResponse<java.util.List<Child>> getAllChildren() {
        java.util.List<Child> sortedChildren = childRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(Child::getFirstName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        return ApiResponse.success("Fetched all children", sortedChildren);
    }
}
