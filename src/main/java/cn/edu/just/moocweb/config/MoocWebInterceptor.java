package cn.edu.just.moocweb.config;

import cn.edu.just.moocweb.entity.SysLog;
import cn.edu.just.moocweb.entity.User;
import cn.edu.just.moocweb.exception.ServiceException;
import cn.edu.just.moocweb.exception.UserException;
import cn.edu.just.moocweb.mapper.SysLogRepository;
import cn.edu.just.moocweb.service.UserService;
import cn.edu.just.moocweb.utils.AESUtil;
import cn.edu.just.moocweb.utils.ErrCode;
import cn.edu.just.moocweb.utils.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.util.Objects;

@Slf4j
@Component
public class MoocWebInterceptor implements HandlerInterceptor {

    @Autowired
    private SysLogRepository sysLogRepository;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, @NotNull Object handler){
        response.setCharacterEncoding("utf-8");
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        SysLog sysLog = new SysLog();
        sysLog.setIp(IPUtils.getIpAddr(request));
        sysLog.setResPath(request.getRequestURI());
        if(user==null) {
            user = LoginWithCookie(request);
        }
        if(user!=null){
            sysLog.setUserId(user.getId());
        }
        sysLogRepository.saveAndFlush(sysLog);
        try {
            if (user == null) {
                response.sendRedirect(request.getContextPath()+"/");
                return false;
            }
            else if(user.isBan()){
                throw new UserException(ErrCode.USER_BAN,"用户被封禁");
            }
        }
        catch (Exception ex){
            throw new ServiceException(ErrCode.UNKNOWN_ERR,"跳转失败");
        }
        return true;
    }

    private User LoginWithCookie(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {//判断Cookie是否为空
                for (Cookie c : cookies) {
                    if ("auth-token".equals(c.getName())) {
                        String token = Objects.requireNonNull(AESUtil.AESDncode(AESUtil.SECRET_KEY, URLDecoder.decode(c.getValue(),"utf-8")));
                        String uuid = token.substring(0,token.indexOf('|'));
                        String pass = token.substring(token.indexOf('|')+1);
                        User user = userService.getRepository().findUserByUuid(uuid);
                        if(user.getPassword().equals(pass)){
                            System.out.println("自动登录");
                            return user;
                        }
                        return null;
                    }
                }
            }
            return null;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
