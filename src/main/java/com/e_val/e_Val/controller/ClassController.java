package com.e_val.e_Val.controller;

import com.e_val.e_Val.model.Class;
import com.e_val.e_Val.model.User;
import com.e_val.e_Val.model.dto.ClassCreationRequest;
import com.e_val.e_Val.model.dto.ClassResponse;
import com.e_val.e_Val.model.enums.Role;
import com.e_val.e_Val.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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