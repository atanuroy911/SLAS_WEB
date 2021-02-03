package com.slas.entity;

import javax.persistence.*;
@javax.persistence.Entity
@Table(name = "lecture_list")
public class LectureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//定义主键
    private int id;
    @Column
    private long card_no;
    @Column
    private String lecture_id;
    @Column
    private String lecture_name;

//    @Column
//    private int count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCard_no() {
        return card_no;
    }

    public void setCard_no(Long card_no) {
        this.card_no = card_no;
    }

    public String getLecture_id() {
        return lecture_id;
    }

    public void setLecture_id(String lecture_id) {
        this.lecture_id = lecture_id;
    }

    public String getLecture_name() {
        return lecture_name;
    }

    public void setLecture_name(String lecture_name) {
        this.lecture_name = lecture_name;
    }

//    public int getCount() {
//        return count;
//    }
//
//    public void setCount(int id) {
//        this.count = count;
//    }
}
