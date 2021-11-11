package cn.edu.just.moocweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController extends BaseController{
    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/loginPage")
    public String loginPage(){
        return "login";
    }
}
