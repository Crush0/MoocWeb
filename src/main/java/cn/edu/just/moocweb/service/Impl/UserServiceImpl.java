package cn.edu.just.moocweb.service.Impl;

import cn.edu.just.moocweb.entity.User;
import cn.edu.just.moocweb.exception.UserException;
import cn.edu.just.moocweb.mapper.UserRepository;
import cn.edu.just.moocweb.service.UserService;
import cn.edu.just.moocweb.utils.EncryptSha256Util;
import cn.edu.just.moocweb.utils.ErrCode;
import cn.edu.just.moocweb.utils.MailSender;
import cn.edu.just.moocweb.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends BaseService implements UserService {
    private final UserRepository userRepository;
    private final RedisUtils redisUtils;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,RedisUtils redisUtils){
        this.userRepository = userRepository;
        this.redisUtils = redisUtils;
    }

    @Override
    public UserRepository getRepository() {
        return userRepository;
    }

    public User login(boolean useEmail,String username, String password){
        User user;
        if(useEmail){
            user = userRepository.findUserByEmail(username);
        }
        else{
            user = userRepository.findUserByUsername(username);
        }
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
        user.setVerify(false);
        verifyEmail(user);
        return userRepository.saveAndFlush(user);
    }

    public void verifyEmail(User user){
        String verifyCode = UUID.randomUUID() + "-" + UUID.nameUUIDFromBytes(user.getUsername().getBytes(StandardCharsets.UTF_8)).toString();
        redisUtils.set(verifyCode,user.getUuid(),30L, TimeUnit.MINUTES);
        try {
            new MailSender.Builder("点击链接验证您的邮箱: http://150.158.169.37/verify?code="+verifyCode,user.getEmail()).Subject("MoocWeb--邮箱验证").send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
