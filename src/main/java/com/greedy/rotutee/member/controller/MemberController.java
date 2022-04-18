package com.greedy.rotutee.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberController {

    @GetMapping("/login")
    public void memberLoginPage() {}

    @GetMapping("/regist")
    public void memberRegistPage() {}

}
