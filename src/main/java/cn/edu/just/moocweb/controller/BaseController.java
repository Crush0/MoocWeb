package cn.edu.just.moocweb.controller;

import cn.edu.just.moocweb.exception.ServiceException;
import cn.edu.just.moocweb.utils.ErrCode;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public abstract class BaseController {
    @ResponseBody
    @ExceptionHandler({ServiceException.class})
    public void exceptionHandler(HttpServletResponse response, Throwable ex) {
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/javascript;charset=utf-8");
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("code", ((ServiceException) ex).getCode() == null ? ErrCode.UNKNOWN_ERR : ((ServiceException) ex).getCode());
            jsonMap.put("message", ex.getMessage());
            response.getWriter().print(JSON.toJSONString(jsonMap));
        } catch (Exception ignored) {
        }
    }

    public Map<String, String> getParamMap(HttpServletRequest request) {
        try {
            request.setCharacterEncoding("utf-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        Map<String, String[]> requestParams = request.getParameterMap();
        Map<String, String> params = new HashMap<>();
        for (String str : requestParams.keySet()) {
            String[] values = requestParams.get(str);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(str, valueStr);
        }
        return params;
    }

    public String JSONResult(Integer code, String message, Map<String, Object> args) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", code);
        res.put("message", message);
        if (args != null)
            res.putAll(args);
        return JSON.toJSONString(res);
    }
}
