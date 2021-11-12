package cn.edu.just.moocweb.controller;

import cn.edu.just.moocweb.entity.User;
import cn.edu.just.moocweb.service.UserService;
import cn.edu.just.moocweb.utils.ErrCode;
import cn.edu.just.moocweb.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
public class VerifyController extends BaseController{
    private final RedisUtils redisUtils;
    private final UserService userService;

    @Autowired
    public VerifyController(RedisUtils redisUtils,UserService userService){
        this.redisUtils = redisUtils;
        this.userService = userService;
    }

    @GetMapping("/verify")
    public void verify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/javascript;charset=utf-8");
        Map<String,String> params = getParamMap(request);
        String code = params.get("code");
        String uuid = (String)redisUtils.get(code);
        if(uuid!=null){
            redisUtils.remove(code);
            User user = userService.getRepository().findUserByUuid(uuid);
            if(user==null){
                response.getWriter().write(JSONResult(ErrCode.USER_NOTFOUND,"未知的用户",null));
            }
            else{
                user.setVerify(true);
                userService.getRepository().saveAndFlush(user);
                response.getWriter().write(JSONResult(ErrCode.SUCCESS,"邮箱验证成功",null));
            }
        }
        else{
            response.getWriter().write(JSONResult(ErrCode.NOT_NULL,"链接过期，请重新验证",null));
        }
    }
}
