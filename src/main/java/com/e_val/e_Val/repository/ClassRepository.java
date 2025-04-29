package com.e_val.e_Val.repository;

import com.e_val.e_Val.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByCode(String code);
    Optional<Class> findByName(String name);
}