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
@Table(name = "units_learn_info")
public class UnitsLearnInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    @Column(name = "member_id",nullable = false)
    private String memberId;

    @Column(name = "classroom_id",nullable = false)
    private Long classroomId;

    @Column(name = "unit_id",nullable = false)
    private Long unitId;

    @Column(name = "learned_video_time_count")
    private Integer learnedVideoTimeCount;

    private Double score;

    @Column(name = "start_time")
    private Date startTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UnitsLearnInfo that = (UnitsLearnInfo) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
