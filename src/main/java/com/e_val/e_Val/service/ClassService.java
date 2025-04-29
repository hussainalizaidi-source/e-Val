package com.e_val.e_Val.service;

import com.e_val.e_Val.model.Class;
import com.e_val.e_Val.model.User;
import com.e_val.e_Val.model.dto.ClassCreationRequest;
import com.e_val.e_Val.model.dto.ClassResponse;
import com.e_val.e_Val.model.enums.Role;
import com.e_val.e_Val.repository.ClassRepository;
import com.e_val.e_Val.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(ClassService.class);

    public ClassResponse createClass(ClassCreationRequest request, String adminEmail) {
        log.info("Creating class with name: {}, code: {}, adminEmail: {}", request.getName(), request.getCode(), adminEmail);

        // Validate admin
        User admin = userRepository.findByEmail(adminEmail)
                .filter(u -> u.getRole() == Role.ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin not found or not authorized"));

        validateClassCreation(request);

        Class newClass = new Class();
        newClass.setName(request.getName());
        newClass.setCode(request.getCode());
        newClass.setSection(request.getSection());
        newClass.setMaxCapacity(request.getMaxCapacity());

        // Assign teacher if provided
        if (request.getTeacherEmail() != null && !request.getTeacherEmail().isEmpty()) {
            User teacher = userRepository.findByEmail(request.getTeacherEmail())
                    .filter(u -> u.getRole() == Role.TEACHER)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            newClass.setTeacher(teacher);
        }

        log.info("Saving class: {}", newClass);
        Class savedClass = classRepository.save(newClass);
        return mapToResponse(savedClass);
    }

    @Transactional
    public ClassResponse assignTeacher(Long classId, String teacherEmail) {
        log.info("Assigning teacher {} to class ID {}", teacherEmail, classId);

        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        User teacher = userRepository.findByEmail(teacherEmail)
                .filter(u -> u.getRole() == Role.TEACHER)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        classEntity.setTeacher(teacher);
        log.info("Saving updated class: {}", classEntity);
        return mapToResponse(classRepository.save(classEntity));
    }

    @Transactional
    public ClassResponse addStudent(Long classId, Long studentId) {
        log.info("Adding student ID {} to class ID {}", studentId, classId);

        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        User student = userRepository.findById(studentId)
                .filter(u -> u.getRole() == Role.STUDENT)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (classEntity.getStudents().size() >= classEntity.getMaxCapacity()) {
            throw new RuntimeException("Class capacity exceeded");
        }

        classEntity.getStudents().add(student);
        log.info("Saving updated class: {}", classEntity);
        return mapToResponse(classRepository.save(classEntity));
    }

    private void validateClassCreation(ClassCreationRequest request) {
        log.info("Validating class creation: name={}, code={}", request.getName(), request.getCode());

        if (classRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Class code already exists");
        }
        if (classRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Class name already exists");
        }
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

    public List<Class> getAllClasses() {
        log.info("Fetching all classes");
        return classRepository.findAll();
    }
}