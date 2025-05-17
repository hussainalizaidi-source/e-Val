package com.e_val.e_Val.controller;

import com.e_val.e_Val.model.Class;
import com.e_val.e_Val.model.User;
import com.e_val.e_Val.model.dto.AssignStudentRequest;
import com.e_val.e_Val.model.dto.ClassCreationRequest;
import com.e_val.e_Val.model.dto.ClassResponse;
import com.e_val.e_Val.model.enums.Role;
import com.e_val.e_Val.repository.ClassRepository;
import com.e_val.e_Val.service.ClassService;
import com.e_val.e_Val.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;
    private final UserService userService;
    private final ClassRepository classRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassResponse> createClass(@RequestBody ClassCreationRequest request,
                                                    @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(classService.createClass(request, admin.getEmail()));
    }

    @PostMapping("/{classId}/assign-teacher")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassResponse> assignTeacher(@PathVariable Long classId,
                                                      @RequestBody String teacherEmail) {
        // Strip quotes if present
        String cleanedEmail = teacherEmail.replaceAll("^\"|\"$", "");
        return ResponseEntity.ok(classService.assignTeacher(classId, cleanedEmail));
    }

    @PostMapping("/{classId}/add-student")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassResponse> addStudent(@PathVariable Long classId,
                                                   @RequestBody Long studentId) {
        return ResponseEntity.ok(classService.addStudent(classId, studentId));
    }

    @GetMapping("/students/rollno/{rollNo}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<User> getStudentByRollNo(@PathVariable String rollNo) {
        User student = userService.findByRollNo(rollNo)
                .filter(u -> u.getRole() == Role.STUDENT)
                .orElseThrow(() -> new RuntimeException("Student not found with roll_no: " + rollNo));
        return ResponseEntity.ok(student);
    }

    @PostMapping("/{classId}/assign-student")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ClassResponse> assignStudentToClass(
            @PathVariable Long classId,
            @RequestBody AssignStudentRequest request,
            Authentication authentication) {
        System.out.println("Attempting to assign student to classId: " + classId + ", rollNo: " + request.getRollNo() + ", teacher: " + authentication.getName());
        String teacherEmail = authentication.getName();
        // Verify the teacher is assigned to the class
        Class classEntity = classService.getClassesByTeacher(teacherEmail).stream()
                .filter(c -> c.getId().equals(classId))
                .findFirst()
                .orElseThrow(() -> {
                    System.out.println("Teacher " + teacherEmail + " not assigned to classId: " + classId);
                    return new RuntimeException("Class not found or not assigned to teacher");
                });
        ClassResponse response = classService.assignStudentByRollNo(classId, request.getRollNo());
        System.out.println("Student assigned successfully to classId: " + classId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-classes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ClassResponse>> getMyClasses(@AuthenticationPrincipal User student) {
        System.out.println("Fetching classes for student: " + student.getEmail());
        List<Class> classes = classService.getClassesByStudent(student.getEmail());
        List<ClassResponse> responses = classes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        System.out.println("Returning " + responses.size() + " classes for student: " + student.getEmail());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<ClassResponse>> getClasses(@AuthenticationPrincipal User user) {
        List<Class> classes;
        if (user.getRole() == Role.TEACHER) {
            classes = classService.getClassesByTeacher(user.getEmail());
        } else {
            classes = classService.getAllClasses();
        }
        List<ClassResponse> responses = classes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<Class>> getAvailableClasses() {
        List<Class> classes = classRepository.findAll().stream()
                .filter(cls -> cls.getStudents().size() < cls.getMaxCapacity())
                .toList();
        return ResponseEntity.ok(classes);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("message", ex.getMessage()));
    }

    private ClassResponse mapToResponse(Class classEntity) {
        ClassResponse response = new ClassResponse();
        response.setId(classEntity.getId());
        response.setName(classEntity.getName());
        response.setCode(classEntity.getCode());
        response.setSection(classEntity.getSection());
        response.setMaxCapacity(classEntity.getMaxCapacity());
        response.setTeacher(classEntity.getTeacher());
        response.setStudents(classEntity.getStudents());
        return response;
    }
}