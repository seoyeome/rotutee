package com.greedy.rotutee.basket.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greedy.rotutee.Authentication.dto.CustomUser;
import com.greedy.rotutee.basket.dto.AttachedFileDTO;
import com.greedy.rotutee.basket.dto.BasketMemberCouponBoxDTO;
import com.greedy.rotutee.basket.dto.ClassBasketDTO;
import com.greedy.rotutee.basket.dto.MemberLectureDTO;
import com.greedy.rotutee.basket.service.BasketService;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName      : com.greedy.rotutee.basket.controller
 * fileName         : BasketController
 * author           : SEOK
 * date             : 2022-05-03
 * description      :
 * ==========================================================
 * DATE            AUTHOR              NOTE
 * ----------------------------------------------------------
 * 2022-05-03      SEOK         최초 생성
 */
@Controller
@RequestMapping("/basket")
public class BasketController {

    private final BasketService basketService;

    @Autowired
    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping("/regist/{lectureNo}")
    public ModelAndView addLecture(ModelAndView mv, @PathVariable int lectureNo
                                                  , @AuthenticationPrincipal CustomUser customUser
                                                  , RedirectAttributes rttr) {

        if(customUser == null) {

            rttr.addFlashAttribute("message", "로그인이 필요한 서비스입니다.");
            mv.setViewName("redirect:/lecture/detail?lectureNo=" + lectureNo);
        } else {

            int memberNo = customUser.getNo();
            ClassBasketDTO basket = basketService.findByLectureNoAndMemberNo(lectureNo, memberNo);
            MemberLectureDTO memberLectureDTO = basketService.findLectureNoAndMemberNo(lectureNo, memberNo);
            if (basket == null && memberLectureDTO == null) {

                basketService.registLectureToCart(lectureNo, memberNo);

                rttr.addFlashAttribute("message", "수강바구니에 추가되었습니다.");
                mv.setViewName("redirect:/lecture/detail?lectureNo=" + lectureNo);

            } else if(memberLectureDTO != null){

                rttr.addFlashAttribute("message", "이미 결제하신 강의입니다.");
                mv.setViewName("redirect:/lecture/detail?lectureNo=" + lectureNo);
            } else {

                rttr.addFlashAttribute("message", "이미 추가되어있습니다.");
                mv.setViewName("redirect:/lecture/detail?lectureNo=" + lectureNo);
            }
        }

        return mv;
    }

    @GetMapping("/list")
    public ModelAndView findBasketList(ModelAndView mv, @AuthenticationPrincipal CustomUser customUser) {

        int memberNo = customUser.getNo();
        List<ClassBasketDTO> cartList = basketService.findByMemberNo(memberNo);

        for(ClassBasketDTO cart : cartList) {
            List<AttachedFileDTO> fileList = cart.getLecture().getImageList();
        }

        List<BasketMemberCouponBoxDTO> coupons = basketService.selectCouponList(memberNo);

        mv.addObject("coupons", coupons);
        mv.addObject("cartList", cartList);
        mv.setViewName("basket/basketlist");

        return mv;
    }

    @GetMapping("/remove/{lectureNo}")
    public ModelAndView removeOneBasket(ModelAndView mv, @PathVariable int lectureNo
                                                       , @AuthenticationPrincipal CustomUser customUser
                                                       , RedirectAttributes rttr) {

        int memberNo = customUser.getNo();

        basketService.removeOneBasket(lectureNo, memberNo);

        rttr.addFlashAttribute("message", "해당 강의를 제거하였습니다.");
        mv.setViewName("redirect:/basket/list");

        return mv;
    }

    @PostMapping("/lectureSuccess")
    public ModelAndView successBasketPay(ModelAndView mv,HttpServletRequest request,@AuthenticationPrincipal CustomUser customUser
            , RedirectAttributes rttr){

        int memberNo = customUser.getNo();
        int lectureNo = Integer.parseInt(request.getParameter("lectureNo"));
        String couponNo = request.getParameter("couponNoinput");

        System.out.println("gg");
        basketService.lectureSuccessBasket(lectureNo,memberNo,couponNo);

        rttr.addFlashAttribute("message", "해당 강의가 결제 되었습니다.");

        mv.setViewName("redirect:/basket/list");

        return mv;
    }


}
