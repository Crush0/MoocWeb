package cn.edu.just.moocweb.controller;

import cn.edu.just.moocweb.entity.User;
import cn.edu.just.moocweb.service.UserService;
import cn.edu.just.moocweb.utils.AESUtil;
import cn.edu.just.moocweb.utils.ErrCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Objects;

@Controller
public class LoginController extends BaseController{
    private final UserService userService;

    @Autowired
    public LoginController(UserService userService){
        this.userService = userService;
    }

    @ResponseBody
    @PostMapping("/login")
    public String userLogin(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");
        User user = userService.login(username,password);
        assert user != null;
        session.setAttribute("user",user);
        if(remember.equals("on")){
            try{
                Cookie usernameCookie = new Cookie("auth-token",
                        URLEncoder.encode(Objects.requireNonNull(AESUtil.AESEncode(AESUtil.SECRET_KEY, user.getUuid() + '|' + user.getPassword())),"utf-8"));
                usernameCookie.setMaxAge(10 * 24 * 60);
                usernameCookie.setPath("/");
                response.addCookie(usernameCookie);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return JSONResult(ErrCode.SUCCESS,"登录成功",new HashMap<String, Object>() {
            {
                put("location", "/");
            }
        });
    }
}
