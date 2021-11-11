package cn.edu.just.moocweb.mapper;

import cn.edu.just.moocweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserByUuid(String uuid);
}