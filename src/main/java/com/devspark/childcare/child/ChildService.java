package com.devspark.childcare.child;

import com.devspark.childcare.auth.Account;
import com.devspark.childcare.auth.AccountRepository;
import com.devspark.childcare.auth.Parent;
import com.devspark.childcare.auth.ParentRepository;
import com.devspark.childcare.child.dto.ChildRegistrationDto;
import com.devspark.childcare.child.dto.ChildResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildService {

    private final ChildRepository childRepository;
    private final ParentRepository parentRepository;
    private final AccountRepository accountRepository;

    public List<ChildResponseDto> getAllChildren() {
        return childRepository.findAll().stream()
                .map(child -> {
                    Parent parent = parentRepository.findById(child.getParentId()).orElse(null);
                    String guardianName = parent != null ? parent.getFullName() : "Unknown";
                    String guardianEmail = (parent != null && parent.getAccount() != null) 
                            ? parent.getAccount().getEmail() : "Unknown";
                    
                    return ChildResponseDto.builder()
                            .childId(child.getChildId())
                            .firstName(child.getFirstName())
                            .lastName(child.getLastName())
                            .dob(child.getDob())
                            .gender(child.getGender().name())
                            .bloodGroup(child.getBloodGroup())
                            .profilePic(child.getProfilePic())
                            .guardianName(guardianName)
                            .guardianEmail(guardianEmail)
                            .status(child.getStatus().name())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void registerChild(ChildRegistrationDto dto) {
        log.info("Registering new child: {} for parent: {}", dto.getFullName(), dto.getParentEmail());

        LocalDate dob = LocalDate.parse(dto.getDob());
        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 3 || age > 10) {
            throw new RuntimeException("Child must be between 3 and 10 years old to be enrolled.");
        }

        // 1. Handle Parent Account
        Account account = accountRepository.findByEmail(dto.getParentEmail())
                .orElseGet(() -> {
                    Account newAccount = Account.builder()
                            .email(dto.getParentEmail())
                            .passwordHash("PRE_REGISTERED") // Placeholder until parent signs up
                            .role(Account.Role.PARENT)
                            .verified(false)
                            .status(Account.Status.INACTIVE)
                            .build();
                    return accountRepository.save(newAccount);
                });

        if (account.getRole() != Account.Role.PARENT) {
            throw new RuntimeException("Email is already registered with a different role.");
        }

        // 2. Handle Parent Profile
        Parent parent = parentRepository.findByAccountAccountId(account.getAccountId())
                .orElseGet(() -> {
                    Parent newParent = Parent.builder()
                            .account(account)
                            .fullName(dto.getParentFullName())
                            .address(dto.getAddress())
                            .phone(dto.getParentContact())
                            .nic(dto.getParentID())
                            .relationship(Parent.Relationship.valueOf(dto.getRelationship().toUpperCase()))
                            .build();
                    return parentRepository.save(newParent);
                });

        // 2.5 Check for duplicates
        if (childRepository.existsByFirstNameAndLastNameAndDobAndParentId(dto.getFullName(), "", dob, parent.getParentId())) {
            throw new RuntimeException("A child with the same name and date of birth is already registered for this parent.");
        }

        // 3. Create Child
        Child child = Child.builder()
                .firstName(dto.getFullName()) // Frontend uses fullName for student first name in simple mode
                .lastName("") // Frontend doesn't split it currently
                .dob(LocalDate.parse(dto.getDob()))
                .gender(Child.Gender.valueOf(dto.getGender().toUpperCase()))
                .bloodGroup(dto.getBloodGroup())
                .height(dto.getHeight() != null ? BigDecimal.valueOf(dto.getHeight()) : null)
                .weight(dto.getWeight() != null ? BigDecimal.valueOf(dto.getWeight()) : null)
                .specialNote(dto.getSpecialNote())
                .profilePic(dto.getProfilePic())
                .parentId(parent.getParentId())
                .build();

        childRepository.save(child);
        log.info("Child {} successfully registered with ID: {}", child.getFirstName(), child.getChildId());
    }

    public ChildResponseDto getChildById(UUID childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));
        
        Parent parent = parentRepository.findById(child.getParentId()).orElse(null);
        String guardianName = parent != null ? parent.getFullName() : "Unknown";
        String guardianEmail = (parent != null && parent.getAccount() != null) 
                ? parent.getAccount().getEmail() : "Unknown";
        
        return ChildResponseDto.builder()
                .childId(child.getChildId())
                .firstName(child.getFirstName())
                .lastName(child.getLastName())
                .dob(child.getDob())
                .gender(child.getGender().name())
                .bloodGroup(child.getBloodGroup())
                .profilePic(child.getProfilePic())
                .guardianName(guardianName)
                .guardianEmail(guardianEmail)
                .status(child.getStatus().name())
                .build();
    }

    @Transactional
    public void updateChild(UUID childId, ChildResponseDto dto) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));
        
        child.setFirstName(dto.getFirstName());
        child.setLastName(dto.getLastName());
        child.setDob(dto.getDob());
        child.setGender(Child.Gender.valueOf(dto.getGender().toUpperCase()));
        child.setBloodGroup(dto.getBloodGroup());
        child.setProfilePic(dto.getProfilePic());
        
        if (dto.getStatus() != null) {
            child.setStatus(ChildStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        childRepository.save(child);
        log.info("Child {} updated successfully", childId);
    }
}
