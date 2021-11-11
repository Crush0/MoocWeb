package cn.edu.just.moocweb.controller;

import cn.edu.just.moocweb.entity.User;
import cn.edu.just.moocweb.exception.UserException;
import cn.edu.just.moocweb.service.UserService;
import cn.edu.just.moocweb.utils.ErrCode;
import cn.edu.just.moocweb.utils.MoocUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;



@Controller
public class BindController extends BaseController{
    private final UserService userService;

    @Autowired
    public BindController(UserService userService){
        this.userService = userService;
    }

    @ResponseBody
    @PostMapping("/bindMooc")
    public void bindMooc(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User)session.getAttribute("user");
        Map<String,String> params = getParamMap(request);
        String moocUser = params.get("mooc_user");
        String moocPwd = params.get("mooc_pwd");
        Integer loginType = Integer.parseInt(params.get("type"));
        if(moocUser==null||moocPwd==null){
            throw new UserException(ErrCode.NOT_NULL, "绑定失败,Mooc用户名或密码不能为空");
        }
        String cookies = MoocUtils.getCookie(moocUser,moocPwd,loginType);
        if (cookies == null) {
            throw new UserException(ErrCode.BIND_ERROR, "绑定失败,无法获得Cookie");
        }
        user.setMoocCookie(cookies);
        userService.getRepository().saveAndFlush(user);
        response.getWriter().write(JSONResult(ErrCode.SUCCESS,"绑定MOOC账号成功",null));
    }

    @ResponseBody
    @PostMapping("/checkMooc")
    public void checkMooc(HttpSession session,HttpServletResponse response) throws IOException{
        User user = (User)session.getAttribute("user");
        String cookieStr = user.getMoocCookie();
        if(cookieStr==null) {
            response.getWriter().write(JSONResult(ErrCode.BIND_ERROR,"请先绑定MOOC账号",null));
        }
        else{
            if(!MoocUtils.canLogin(cookieStr)){
                response.getWriter().write(JSONResult(ErrCode.BIND_ERROR,"Cookie过期，请重新绑定",null));
            }
            else{
                response.getWriter().write(JSONResult(ErrCode.SUCCESS,"",null));
            }
        }

    }
}
