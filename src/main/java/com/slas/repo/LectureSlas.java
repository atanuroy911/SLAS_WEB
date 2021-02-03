package com.slas.repo;

import com.slas.entity.LectureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;


public interface LectureSlas extends JpaRepository<LectureEntity, Integer> {
}
