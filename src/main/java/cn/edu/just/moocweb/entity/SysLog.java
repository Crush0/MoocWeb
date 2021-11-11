package cn.edu.just.moocweb.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sys_log")
public class SysLog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long Id;
    @Column(name = "ip",nullable = false)
    private String ip;
    @Column(name = "res_path",nullable = false)
    private String resPath;
    @Column(name = "user_id")
    private Long userId;

    @CreatedDate
    @Column(name = "create_time")
    private Date createTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SysLog sysLog = (SysLog) o;
        return Id != null && Objects.equals(Id, sysLog.Id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
