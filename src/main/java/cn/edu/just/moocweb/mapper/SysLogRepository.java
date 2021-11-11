package cn.edu.just.moocweb.mapper;

import cn.edu.just.moocweb.entity.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysLogRepository extends JpaRepository<SysLog,Long> {
}
