package cn.edu.just.moocweb.controller;

import cn.edu.just.moocweb.entity.User;
import cn.edu.just.moocweb.service.UserService;
import cn.edu.just.moocweb.utils.ErrCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController extends BaseController{
    private final UserService userService;

    @Autowired
    public LoginController(UserService userService){
        this.userService = userService;
    }

    @ResponseBody
    @PostMapping("/login")
    public String userLogin(HttpServletRequest request){
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");
        User user = userService.login(username,password);
        session.setAttribute("user",user);
        if(remember!=null){

        }
        return JSONResult(ErrCode.SUCCESS,"登录成功",null);
    }
}
