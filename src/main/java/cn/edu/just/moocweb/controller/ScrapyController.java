package cn.edu.just.moocweb.controller;

import cn.edu.just.moocweb.entity.User;
import cn.edu.just.moocweb.service.ScrapyService;
import cn.edu.just.moocweb.utils.ErrCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ScrapyController extends BaseController{

    private final ScrapyService scrapyService;

    @Autowired
    public ScrapyController(ScrapyService scrapyService){
        this.scrapyService = scrapyService;
    }

    @PostMapping("/api/getcourse")
    public void getCourse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        User user = (User)request.getSession().getAttribute("user");
        if(user.getMoocCookie()==null){
            response.getWriter().write(JSONResult(ErrCode.SUCCESS,"请求失败，请先绑定MOOC账号",null));
            return;
        }
        String threadId = scrapyService.spiderCourse(user.getId());
        response.getWriter().write(JSONResult(ErrCode.SUCCESS,"请求成功",new HashMap<String,Object>(){{
            put("thread-id",threadId);
        }}));
    }

    @PostMapping("/api/getClassroom")
    public void getClassroom(HttpServletRequest request,HttpServletResponse response) throws IOException{
        response.setCharacterEncoding("utf-8");
        int classroomId = 0;
        try {
            classroomId = Integer.parseInt(getParamMap(request).get("classroom-id"));
        }
        catch (Exception ex){
            response.getWriter().write(JSONResult(ErrCode.NOT_NULL,"请求失败，参数错误",null));
        }
        User user = (User)request.getSession().getAttribute("user");
        if(user.getMoocCookie()==null){
            response.getWriter().write(JSONResult(ErrCode.SUCCESS,"请求失败，请先绑定MOOC账号",null));
            return;
        }
        String threadId = scrapyService.spiderClassroom(user.getId(),classroomId);
        response.getWriter().write(JSONResult(ErrCode.SUCCESS,"请求成功",new HashMap<String,Object>(){{
            put("thread-id",threadId);
        }}));
    }

    @PostMapping("/api/check")
    public void checkThread(HttpServletRequest request,HttpServletResponse response) throws IOException{
        response.setCharacterEncoding("utf-8");
        Map<String,String> params = getParamMap(request);
        String threadId = params.get("thread-id");
        Integer status = scrapyService.checkThread(threadId);
        response.getWriter().write(JSONResult(ErrCode.SUCCESS,"请求成功",new HashMap<String,Object>(){{
            put("thread-id",threadId);
            put("status",status);
        }}));
    }
}
