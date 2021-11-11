package cn.edu.just.moocweb.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "mooc_student")
public class Student implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    @Column(name = "member_id",nullable = false)
    private String memberId;

    @Column(name = "nickname",nullable = false)
    private String nickname;

    @Column(name = "number")
    private String number;

    @Column(name = "classroom_id",nullable = false)
    private String classroomId;

    @Column(name = "real_name",nullable = false)
    private String realName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Student student = (Student) o;
        return id != null && Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
