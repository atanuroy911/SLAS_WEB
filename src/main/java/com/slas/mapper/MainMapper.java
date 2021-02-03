package com.slas.mapper;

import com.slas.entity.LectureEntity;
import com.slas.entity.TeacherAuthEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author hxc
 * @version 1.0
 * @date 2019/11/7 12:10
 */
@Mapper
public interface MainMapper {

    @Select("select * from teacher_auth where card_no=#{card_no}")
    TeacherAuthEntity GetUserByCard_no(long card_no);

    @Select("select * from lecture_list where id=#{id}")
    LectureEntity GetLectureById(long id);

}
