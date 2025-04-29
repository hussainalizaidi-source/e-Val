package com.e_val.e_Val.controller;

import com.e_val.e_Val.model.User;
import com.e_val.e_Val.model.dto.ClassCreationRequest;
import com.e_val.e_Val.model.dto.ClassResponse;
import com.e_val.e_Val.service.ClassService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
                                              @RequestBody String teacherEmail,
                                              @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(classService.assignTeacher(classId, teacherEmail.replaceAll("\"", "")));
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<com.e_val.e_Val.model.Class>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }
    @PutMapping("/{classId}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassResponse> addStudent(
            @PathVariable Long classId,
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(classService.addStudent(classId, studentId));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleExceptions(RuntimeException ex) {
        return ResponseEntity.badRequest().body(
            Collections.singletonMap("message", ex.getMessage())
        );
    }
}