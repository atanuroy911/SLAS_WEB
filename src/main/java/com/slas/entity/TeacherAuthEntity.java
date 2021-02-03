package com.slas.entity;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.Objects;

/**
 * @author hxc
 * @version 1.0
 * @date 2019/11/4 10:59
 */

@Entity
@Table(name = "teacher_auth")
public class TeacherAuthEntity {

    private int id;
    private Long card_no;
    private String name_full;
    private long phn_no;
    private String pwd_no;
    private String admin_pwd;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "card_no")
    public Long getCard_no() {
        return card_no;
    }

    public void setCard_no(Long card_no) {
        this.card_no = card_no;
    }

    @Basic
    @Column(name = "name_full")
    public String getName_full() {
        return name_full;
    }

    public void setName_full(String name_full) {
        this.name_full = name_full;
    }

    @Basic
    @Column(name = "phn_no")
    public long getPhn_no() {
        return phn_no;
    }

    public void setPhn_no(long phn_no) {
        this.phn_no = phn_no;
    }

    @Basic
    @Column(name = "pwd_no")
    public String getPwd_no() {
        return pwd_no;
    }

    public void setPwd_no(String pwd_no) {
        this.pwd_no = pwd_no;
    }

    @Basic
    @Column(name = "admin_pwd")
    public String getAdmin_pwd() {
        return admin_pwd;
    }

    public void setAdmin_pwd(String admin_pwd) {
        this.admin_pwd = admin_pwd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeacherAuthEntity that = (TeacherAuthEntity) o;
        return id == that.id &&
                phn_no == that.phn_no &&
                admin_pwd.equals(that.admin_pwd) &&
                card_no.equals(that.card_no) &&
                name_full.equals(that.name_full) &&
                pwd_no.equals(that.pwd_no);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, card_no, name_full, phn_no, pwd_no, admin_pwd);
    }

    @Override
    public String toString() {
        return "TeacherAuthEntity{" +
                "id=" + id +
                ", card_no=" + card_no +
                ", name_full='" + name_full + '\'' +
                ", phn_no=" + phn_no +
                ", pwd_no='" + pwd_no + '\'' +
                ", admin_pwd=" + admin_pwd +
                '}';
    }
}
