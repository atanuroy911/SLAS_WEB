package com.slas.repo;

import com.slas.entity.TeacherAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author hxc
 * @version 1.0
 * @date 2019/11/4 11:00
 */

public interface RepoSlas extends JpaRepository<TeacherAuthEntity,Integer> {
   // TeacherAuthEntity findByCard_noLike(long card_no);
}
