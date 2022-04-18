package com.greedy.rotutee.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @GetMapping(value = {"/", "/main"})
    public String main() {

        return "/main/main";
    }

    @PostMapping("/")
    public String redirectMain() {

        return "redirect:/";
    }

    @GetMapping("/study")
    public String studyList(){
        System.out.println("스터디 요청");
        return "/study/studyList";
    }
}
