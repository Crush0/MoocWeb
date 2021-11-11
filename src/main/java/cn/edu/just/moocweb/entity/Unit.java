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
@Table
public class Unit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    @Column(name = "avg_duration")
    private Double avgDuration;

    @Column(name = "content_id")
    private String contentId;

    @Column(name = "content_type")
    private Integer contentType;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "view_member_count")
    private Integer viewMemberCount;

    @Column(name = "view_times_avg_count")
    private Double viewTimesAvgCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Unit unit = (Unit) o;
        return id != null && Objects.equals(id, unit.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
