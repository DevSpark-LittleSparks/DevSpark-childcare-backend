package com.devspark.childcare.auth;

import com.devspark.childcare.auth.dto.PendingRequestDto;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.devspark.childcare.staff.Teacher;
import com.devspark.childcare.staff.TeacherRepository;
import java.util.List;
import java.util.Map;
import com.devspark.childcare.auth.Parent;
import com.devspark.childcare.auth.ParentRepository;
import com.devspark.childcare.auth.AccountRepository;
import com.devspark.childcare.child.ChildRepository;


@RestController
@RequestMapping("/api/v1/auth/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SignupService signupService;
    private final AdminDashboardService adminDashboardService;
    private final AdminProfileService adminProfileService;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final AccountRepository accountRepository;
    private final ChildRepository childRepository;

    // ─── Profile Management ───────────────    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<com.devspark.childcare.auth.dto.AdminProfileDto> getProfile(java.security.Principal principal) {
        return com.devspark.childcare.shared.response.ApiResponse.success("Admin profile retrieved", adminProfileService.getAdminProfile(principal.getName()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> updateProfile(@RequestBody com.devspark.childcare.auth.dto.AdminProfileDto dto, java.security.Principal principal) {
        adminProfileService.updateAdminProfile(principal.getName(), dto);
        return com.devspark.childcare.shared.response.ApiResponse.success("Admin profile updated successfully", null);
    }

    // ─── Dashboard Stats & Broadcast ──────────────────────────────────────

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<Map<String, Long>> getDashboardStats() {
        return com.devspark.childcare.shared.response.ApiResponse.success("Dashboard statistics", adminDashboardService.getDashboardStats());
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> broadcastAnnouncement(@RequestParam String title, @RequestParam String content) {
        adminDashboardService.broadcastAnnouncement(title, content);
        return com.devspark.childcare.shared.response.ApiResponse.success("Announcement broadcasted successfully to all parents.", null);
    }

    // ─── Approve ─────────────────────────────────────────────────────────

    @PostMapping("/approve-teacher/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> approveTeacher(@PathVariable String requestId) {
        signupService.approveTeacherRequest(requestId);
        return com.devspark.childcare.shared.response.ApiResponse.success("Teacher request approved. OTP sent to applicant email.", null);
    }

    @PostMapping("/approve-parent/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> approveParent(@PathVariable String requestId) {
        signupService.approveParentRequest(requestId);
        return com.devspark.childcare.shared.response.ApiResponse.success("Parent request approved. OTP sent to applicant email.", null);
    }

    @PostMapping("/approve-director/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> approveDirector(@PathVariable String requestId) {
        signupService.approveDirectorRequest(requestId);
        return com.devspark.childcare.shared.response.ApiResponse.success("Director request approved. OTP sent to applicant email.", null);
    }

    // ─── Reject ───────────────────────────────────────────────────────────

    @PostMapping("/reject-teacher/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> rejectTeacher(@PathVariable String requestId,
                                              @RequestParam(defaultValue = "Your application was not approved.") String reason) {
        signupService.rejectTeacherRequest(requestId, reason);
        return com.devspark.childcare.shared.response.ApiResponse.success("Teacher request rejected.", null);
    }

    @PostMapping("/reject-parent/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> rejectParent(@PathVariable String requestId,
                                             @RequestParam(defaultValue = "Your application was not approved.") String reason) {
        signupService.rejectParentRequest(requestId, reason);
        return com.devspark.childcare.shared.response.ApiResponse.success("Parent request rejected.", null);
    }

    @PostMapping("/reject-director/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<String> rejectDirector(@PathVariable String requestId,
                                               @RequestParam(defaultValue = "Your application was not approved.") String reason) {
        signupService.rejectDirectorRequest(requestId, reason);
        return com.devspark.childcare.shared.response.ApiResponse.success("Director request rejected.", null);
    }

    // ─── Pending Requests ─────────────────────────────────────────────────

    @GetMapping("/pending-teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<List<PendingRequestDto>> getPendingTeachers() {
        return com.devspark.childcare.shared.response.ApiResponse.success("Pending teacher requests", signupService.getPendingTeacherRequests());
    }

    @GetMapping("/pending-parents")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<List<PendingRequestDto>> getPendingParents() {
        return com.devspark.childcare.shared.response.ApiResponse.success("Pending parent requests", signupService.getPendingParentRequests());
    }

    @GetMapping("/pending-directors")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<List<PendingRequestDto>> getPendingDirectors() {
        return com.devspark.childcare.shared.response.ApiResponse.success("Pending director requests", signupService.getPendingDirectorRequests());
    }

    @GetMapping("/all-teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<List<Teacher>> getAllTeachers() {
        List<Teacher> sortedTeachers = teacherRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(Teacher::getFullName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        return com.devspark.childcare.shared.response.ApiResponse.success("Fetched all teachers", sortedTeachers);
    }

    @GetMapping("/all-parents")
    @PreAuthorize("hasRole('ADMIN')")
    public com.devspark.childcare.shared.response.ApiResponse<List<Parent>> getAllParents() {
        List<Parent> sortedParents = parentRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(Parent::getFullName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        return com.devspark.childcare.shared.response.ApiResponse.success("Fetched all parents", sortedParents);
    }

    // ─── Soft Delete ─────────────────────────────────────────────────────

    @DeleteMapping("/parent/{parentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteParent(@PathVariable String parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found"));
        parent.setDeleted(true);
        parent.setDeletedAt(java.time.LocalDateTime.now());
        parentRepository.save(parent);

        // Also soft-delete the linked account
        parent.getAccount().setDeleted(true);
        parent.getAccount().setDeletedAt(java.time.LocalDateTime.now());
        accountRepository.save(parent.getAccount());

        return ApiResponse.success("Parent deleted successfully.", null);
    }

    @DeleteMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteTeacher(@PathVariable String teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        teacher.setDeleted(true);
        teacher.setDeletedAt(java.time.LocalDateTime.now());
        teacherRepository.save(teacher);

        teacher.getAccount().setDeleted(true);
        teacher.getAccount().setDeletedAt(java.time.LocalDateTime.now());
        accountRepository.save(teacher.getAccount());

        return ApiResponse.success("Teacher deleted successfully.", null);
    }

    @GetMapping("/child/{childId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<com.devspark.childcare.auth.dto.AdminChildViewDto> getChild(@PathVariable String childId) {
        com.devspark.childcare.child.Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));
        
        Parent parent = null;
        if (child.getParentId() != null) {
            parent = parentRepository.findById(child.getParentId()).orElse(null);
        } else {
            // Try finding by guardian email if account/parent was pre-created
            parent = parentRepository.findByAccountEmail(child.getGuardianEmail()).orElse(null);
        }

        com.devspark.childcare.auth.dto.AdminChildViewDto dto = com.devspark.childcare.auth.dto.AdminChildViewDto.builder()
                .childId(child.getChildId())
                .firstName(child.getFirstName())
                .lastName(child.getLastName())
                .dob(child.getDob())
                .gender(child.getGender().name())
                .bloodGroup(child.getBloodGroup())
                .height(child.getHeight() != null ? child.getHeight().toString() : "")
                .weight(child.getWeight() != null ? child.getWeight().toString() : "")
                .specialNote(child.getSpecialNote())
                .profileImage(child.getProfilePic())
                .parentFullName(parent != null ? parent.getFullName() : child.getGuardianName())
                .parentEmail(parent != null ? parent.getAccount().getEmail() : child.getGuardianEmail())
                .parentContact(parent != null ? parent.getPhone() : "")
                .parentID(parent != null ? parent.getNic() : "")
                .address(parent != null ? parent.getAddress() : "")
                .relationship(parent != null && parent.getRelationship() != null ? parent.getRelationship().name() : "GUARDIAN")
                .build();

        return ApiResponse.success("Child record retrieved", dto);
    }

    @PutMapping("/child/{childId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateChild(@PathVariable String childId, @RequestBody com.devspark.childcare.child.Child childData) {
        com.devspark.childcare.child.Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));
        
        // Update fields
        child.setFirstName(childData.getFirstName());
        child.setLastName(childData.getLastName());
        child.setDob(childData.getDob());
        child.setGender(childData.getGender());
        child.setBloodGroup(childData.getBloodGroup());
        child.setHeight(childData.getHeight());
        child.setWeight(childData.getWeight());
        child.setAddress(childData.getAddress());
        child.setSpecialNote(childData.getSpecialNote());
        child.setGuardianName(childData.getGuardianName());
        child.setGuardianEmail(childData.getGuardianEmail());
        
        childRepository.save(child);
        return ApiResponse.success("Child record updated successfully.", null);
    }

    @DeleteMapping("/child/{childId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteChild(@PathVariable String childId) {
        com.devspark.childcare.child.Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found"));
        child.setDeleted(true);
        child.setDeletedAt(java.time.LocalDateTime.now());
        childRepository.save(child);
        return ApiResponse.success("Child record deleted successfully.", null);
    }
}

