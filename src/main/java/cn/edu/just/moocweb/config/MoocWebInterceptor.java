package cn.edu.just.moocweb.config;

import cn.edu.just.moocweb.entity.SysLog;
import cn.edu.just.moocweb.entity.User;
import cn.edu.just.moocweb.exception.ServiceException;
import cn.edu.just.moocweb.exception.UserException;
import cn.edu.just.moocweb.mapper.SysLogRepository;
import cn.edu.just.moocweb.utils.ErrCode;
import cn.edu.just.moocweb.utils.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class MoocWebInterceptor implements HandlerInterceptor {

    @Autowired
    private SysLogRepository sysLogRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        response.setCharacterEncoding("utf-8");
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        SysLog sysLog = new SysLog();
        sysLog.setIp(IPUtils.getIpAddr(request));
        sysLog.setResPath(request.getRequestURI());
        if(user!=null){
            sysLog.setUserId(user.getId());
        }
        sysLogRepository.saveAndFlush(sysLog);
        try {
            if (user == null) {
                response.sendRedirect(request.getContextPath()+"/loginPage");
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
}
