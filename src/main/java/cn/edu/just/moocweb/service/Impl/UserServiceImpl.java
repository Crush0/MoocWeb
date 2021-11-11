package cn.edu.just.moocweb.service.Impl;

import cn.edu.just.moocweb.entity.User;
import cn.edu.just.moocweb.exception.UserException;
import cn.edu.just.moocweb.mapper.UserRepository;
import cn.edu.just.moocweb.service.UserService;
import cn.edu.just.moocweb.utils.EncryptSha256Util;
import cn.edu.just.moocweb.utils.ErrCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class UserServiceImpl extends BaseService implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserRepository getRepository() {
        return userRepository;
    }

    public User login(String username, String password){
        User user = userRepository.findUserByUsername(username);
        if(user==null){
            throw new UserException(ErrCode.USER_NOTFOUND,"用户名不存在");
        }
        else if(!user.getPassword().equals(EncryptSha256Util.Encrypt(user.getUuid()+password))){
            throw new UserException(ErrCode.PWD_NOT_MATCH,"密码错误");
        }
        if(user.isBan()){
            throw new UserException(ErrCode.USER_BAN,"用户被封禁");
        }
        return user;
    }

    public User register(User user){
        if(userRepository.findUserByUsername(user.getUsername())!=null){
            throw new UserException(ErrCode.USER_EXIST,"用户名已存在");
        }
        if(userRepository.findUserByEmail(user.getEmail())!=null){
            throw new UserException(ErrCode.EMAIL_EXIST,"邮箱地址已存在");
        }
        user.setUuid(UUID.randomUUID().toString());
        user.setPassword(EncryptSha256Util.Encrypt(user.getUuid()+user.getPassword()));
        user.setCreateTime(new Date());
        user.setBan(false);
        return userRepository.saveAndFlush(user);
    }
}
