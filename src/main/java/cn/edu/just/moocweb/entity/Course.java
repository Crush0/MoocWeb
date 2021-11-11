package cn.edu.just.moocweb.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "mooc_course")
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "school_id",nullable = false)
    private Long schoolId;

    @Column(name = "course_id",nullable = false)
    private Long courseId;

    @Column(name = "course_name",nullable = false)
    private String courseName;

    private boolean canTeach;

    private Long classroomId;

    private String classroomTime;

    private String collegeId;

    private String courseMode;

    private String creatorName;

    private Integer enrollCount;

    private String entranceCode;

    private Integer lessonCount;

    private Date linkOnlineTermEndTime;

    private Long linkOnlineTermId;

    private Date linkOnlineTermStartTime;

    private Long termId;

    private Integer type;

    private Integer webVisible;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Course course = (Course) o;
        return id != null && Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
