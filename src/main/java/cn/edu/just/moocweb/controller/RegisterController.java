package cn.edu.just.moocweb.controller;

import cn.edu.just.moocweb.entity.User;
import cn.edu.just.moocweb.exception.ServiceException;
import cn.edu.just.moocweb.service.UserService;
import cn.edu.just.moocweb.utils.ErrCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class RegisterController extends BaseController{
    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService){
        this.userService = userService;
    }

    @ResponseBody
    @PostMapping("/register")
    public void register(HttpServletRequest request, HttpServletResponse response,HttpSession session) throws IOException{
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/javascript;charset=utf-8");
        Map<String,String> params = getParamMap(request);
        String username = params.get("username");
        String password = params.get("password");
        String email = params.get("email");
        String twice_pwd = params.get("twice");
        PrintWriter out = response.getWriter();
        if(username==null||password==null||email==null||twice_pwd==null){
            out.write(JSONResult(ErrCode.NOT_NULL,"注册信息不能为空",null));
            return;
        }
        if(checkInvalid(username,password,twice_pwd,email,response)){
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            try {
                userService.register(user);
            }
            catch (ServiceException ex){
                out.write(JSONResult(ex.getCode(),ex.getMessage(),null));
                return;
            }
            out.write(JSONResult(ErrCode.SUCCESS,"注册成功",null));
        }
    }

    public boolean checkInvalid(String username,String password,String twice,String email,HttpServletResponse response) throws IOException {
        String userPattern = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";
        String pwdPattern = "^(?![a-zA-z]+$)(?!\\d+$)(?![!@#$%^&*]+$)[a-zA-Z\\d!@#$%^&*]+$";
        String emailPattern = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        PrintWriter out = response.getWriter();
        if(!password.equals(twice)){
            out.write(JSONResult(ErrCode.TWICE_PWD_NOT_MATCH,"两次密码输入不一致",null));
            return false;
        }
        else if(!Pattern.matches(userPattern,username)){
            out.write(JSONResult(ErrCode.USERNAME_INVALID,"用户名格式错误",null));
            return false;
        }
        else if(!Pattern.matches(pwdPattern,password)){
            out.write(JSONResult(ErrCode.PASSWORD_INVALID,"密码格式错误",null));
            return false;
        }
        else if(!Pattern.matches(emailPattern,email)){
            out.write(JSONResult(ErrCode.EMAIL_INVALID,"邮箱格式错误",null));
            return false;
        }
        return true;
    }
}
