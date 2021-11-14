package cn.edu.just.moocweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class PageController extends BaseController{
    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/MyHome")
    public String loginPage(){
        return "main/index";
    }

    @GetMapping("/robots.txt")
    public void robots(HttpServletResponse response) throws IOException {
        response.getWriter().println("User-agent: *\nDisallow: /");
    }
}
