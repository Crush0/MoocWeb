package cn.edu.just.moocweb.service;

import cn.edu.just.moocweb.entity.User;
import cn.edu.just.moocweb.mapper.UserRepository;

public interface UserService {
    UserRepository getRepository();
    User login(String username, String password);
    User register(User user);
}
