package com.e_val.e_Val.repository;

import com.e_val.e_Val.model.Class;
import com.e_val.e_Val.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByCode(String code);
    Optional<Class> findByName(String name);
    List<Class> findByTeacher(User teacher);
}