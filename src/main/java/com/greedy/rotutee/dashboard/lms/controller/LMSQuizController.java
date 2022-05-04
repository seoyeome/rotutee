package com.greedy.rotutee.dashboard.lms.controller;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greedy.rotutee.Authentication.dto.CustomUser;
import com.greedy.rotutee.dashboard.lms.dto.LMSChapterDTO;
import com.greedy.rotutee.dashboard.lms.dto.LMSQuizDTO;
import com.greedy.rotutee.dashboard.lms.dto.LMSQuizStatusDTO;
import com.greedy.rotutee.dashboard.lms.service.LMSQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName : com.greedy.rotutee.dashboard.lms.controller
 * fileName : LMSQuizController
 * author : SeoYoung
 * date : 2022-04-28
 * description :
 * ===========================================================
 * DATE AUTHOR NOTE
 * -----------------------------------------------------------
 * 2022-04-28 SeoYoung 최초 생성
 */
@Controller
@RequestMapping("/quiz")
public class LMSQuizController {

    private LMSQuizService lmsQuizService;

    @Autowired
    public LMSQuizController(LMSQuizService lmsQuizService) {
        this.lmsQuizService = lmsQuizService;
    }


    @ResponseBody
    @GetMapping(value = "/list", produces = "application/json; charset=UTF-8")
    public String findQuizList(HttpServletRequest request) {

        HttpSession session = request.getSession();
        int lectureNo = Integer.parseInt(String.valueOf(session.getAttribute("lectureNo")));
        int quizNo = Integer.parseInt(request.getParameter("quizNo"));

        LMSQuizDTO quizDetail = lmsQuizService.findQuizDetail(quizNo);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss:SSS")
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .serializeNulls().disableHtmlEscaping()
                .create();

        return gson.toJson(quizDetail);

    }

    @GetMapping(value = "/confirm", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public String confirmQuiz(HttpServletRequest request, @AuthenticationPrincipal CustomUser customUser) {
        int quizNo = Integer.parseInt(request.getParameter("quizNo"));

        boolean confirmResult = lmsQuizService.confirmQuiz(quizNo);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss:SSS")
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .serializeNulls().disableHtmlEscaping()
                .create();

        return gson.toJson(confirmResult);
    }

    @PostMapping(value = "/grading", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public String gradingQuiz(HttpServletRequest request, @AuthenticationPrincipal CustomUser customUser) {

        int memberNo = customUser.getNo();
        int answer = Integer.parseInt(request.getParameter("option"));
        int quizNo = Integer.parseInt(request.getParameter("quizNo"));
        HttpSession session = request.getSession();
        int lectureNo = Integer.parseInt(String.valueOf(session.getAttribute("lectureNo")));

        System.out.println("answer넘어오니? = " + answer);

        boolean gradingResult = lmsQuizService.gradingQuiz(answer, memberNo, quizNo, lectureNo);

//        if(gradingResult){
//            rttr.addFlashAttribute("message", "정답입니다.");
//        } else {
//            rttr.addFlashAttribute("message", " 오답입니다.");
//        }

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss:SSS")
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .serializeNulls().disableHtmlEscaping()
                .create();

        return gson.toJson(gradingResult);

    }

    @GetMapping("/status")
    public ModelAndView findQuizStatus(ModelAndView mv, HttpServletRequest request, @AuthenticationPrincipal CustomUser customUser) {
        int memberNo = customUser.getNo();
        HttpSession session = request.getSession();
        int lectureNo = Integer.parseInt(String.valueOf(session.getAttribute("lectureNo")));
        LMSQuizStatusDTO quizStatus = lmsQuizService.findQuizStatus(memberNo, lectureNo);
        /* 퀴즈만 따로 담아줌 */
        List<LMSQuizDTO> quizList = new ArrayList<>();
        for(int i = 0; i < quizStatus.getChapters().size(); i++){
            LMSChapterDTO chapter = quizStatus.getChapters().get(i);
            for(int j = 0; j < chapter.getClassesList().size(); j++) {
                LMSQuizDTO quiz = chapter.getClassesList().get(j).getQuiz();
                quizList.add(quiz);
            }
        }



        mv.addObject("quizList", quizList);
        mv.addObject("quizStatus", quizStatus);
        mv.setViewName("dashboard/lms/quizstatus");
        return mv;
    }
}