package com.e_val.e_Val.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_val.e_Val.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}