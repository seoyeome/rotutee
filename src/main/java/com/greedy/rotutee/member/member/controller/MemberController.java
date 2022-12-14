package com.greedy.rotutee.member.member.controller;

import com.greedy.rotutee.Authentication.dto.CustomUser;
import com.greedy.rotutee.common.paging.Pagenation;
import com.greedy.rotutee.common.paging.PagingButtonInfo;
import com.greedy.rotutee.member.member.dto.*;
import com.greedy.rotutee.member.member.service.MemberService;
import com.greedy.rotutee.member.profile.dto.AttachedFileDTO;
import com.greedy.rotutee.member.profile.dto.TutorInfoDTO;
import com.greedy.rotutee.member.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;

    @Autowired
    public MemberController(MemberService memberService, PasswordEncoder passwordEncoder, ProfileService profileService) {

        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
    }

    @GetMapping("/login")
    public String memberLoginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {

//        String referer = (String)request.getHeader("REFERER");
//        System.out.println("referer = " + referer);
//        Cookie[] url = request.getCookies();
//        if(afterUrl != null) {
//            afterUrl[0].setMaxAge(0);
//        }
//        if("http://127.0.0.1:8001/member/login".equals(referer)) {
//            referer = "http://127.0.0.1:8001/";
//        }
//        System.out.println("get" + request.getRequestURI());
//        Cookie url = new Cookie("url", request.getRequestURI());
//        response.addCookie(url);

        return "/member/login";
    }

    @GetMapping("/logoutSuccess")
    public void memberLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.sendRedirect("/error/logout");
    }

    @GetMapping("/regist")
    public ModelAndView memberRegistPage(ModelAndView mv) {

        mv.addObject("categoryList", memberService.findLectureCategoryList());
        mv.setViewName("/member/regist");

        return mv;
    }

    @GetMapping("/secession")
    public void memberSecessionPage() {}

    @PostMapping("/regist")
    public String registMember(@ModelAttribute MemberDTO member, @RequestParam("categoryNo") int[] categoryNo, RedirectAttributes rttr) {

        //????????? ?????????
        member.setRouletteChance(0);
        member.setRegistrationDate(new Date(System.currentTimeMillis()));
        member.setLeaveStatusYn("N");

        memberService.registMember(member, categoryNo);

        rttr.addFlashAttribute("message", "??????????????? ????????????????????? ???????????? ????????????");

        return "redirect:/member/login";
    }

    @GetMapping(value = "/lecture/category", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<LectureCategoryDTO> findLectureCategoryList() {

        return memberService.findLectureCategoryList();
    }

    @GetMapping("/duplicate")
    @ResponseBody
    public boolean duplicateEmail(@RequestParam String checkEmail) {

        return memberService.duplicateEmail(checkEmail);
    }

    @GetMapping("/findpwd")
    public void findMemberPwd() {}

    @PostMapping("/findpwd")
    public String findMemberPwd(@ModelAttribute MemberDTO member, RedirectAttributes rttr) {

        memberService.findMemberPwd(member);

        rttr.addFlashAttribute("message", "???????????? ????????? ????????????????????? ???????????? ????????????");

        return "redirect:/member/login";
    }

    @GetMapping("/modifypwd")
    public void modifypwdPage() {}

    @PostMapping("/modifypwd")
    public String modifyPassword(@RequestParam("password") String password, @RequestParam("modifyPassword") String modifyPassword,
                                 @AuthenticationPrincipal CustomUser loginMember, RedirectAttributes rttr) {

        if(!passwordEncoder.matches(password, loginMember.getPwd())) {

            rttr.addFlashAttribute("message", "??????????????? ???????????? ????????????.");

            return "redirect:/member/modifypwd";
        }

        memberService.modifyPassword(loginMember, modifyPassword);

        rttr.addFlashAttribute("message", "??????????????? ????????????????????? ?????? ????????? ????????????");

        return "redirect:/member/logout";
    }

    @GetMapping("/list")
    public ModelAndView findMemberList(ModelAndView mv, @PageableDefault Pageable pageable) {

        Page<MemberDTO> memberList = memberService.findAllMember(pageable);
        PagingButtonInfo paging = Pagenation.getPagingButtonInfo(memberList);

        mv.addObject("memberList", memberList);
        mv.addObject("paging", paging);

        mv.setViewName("/member/list");

        return mv;
    }

    @GetMapping("/search")
    public ModelAndView searchMemberList(ModelAndView mv,@RequestParam("searchCondition") String searchCondition,
                                         @RequestParam("searchValue") String searchValue, @PageableDefault Pageable pageable) {

        Page<MemberDTO> memberList = memberService.findSearchMemberList(searchCondition, searchValue, pageable);
        PagingButtonInfo paging = Pagenation.getPagingButtonInfo(memberList);

        mv.addObject("memberList", memberList);
        mv.addObject("paging", paging);

        mv.setViewName("/member/list");

        return mv;
    }
    @ResponseBody
    @GetMapping("/detail")
    public Map<String, Object> findMemberDetail(ModelAndView mv, @RequestParam("memberNo") int memberNo) {

        MemberDTO member = memberService.findMember(memberNo);
        AttachedFileDTO attachedFile = profileService.findMemberProfile(memberNo);
        MemberStatusHistoryDTO memberStatus = memberService.findMemberStatus(memberNo);

        Map<String, Object> memberDetailMap = new HashMap<>();

        memberDetailMap.put("member", member);
        memberDetailMap.put("attachedFile", attachedFile);
        memberDetailMap.put("memberStatus", memberStatus);
        memberDetailMap.put("memberRole", member.getMemberRoleList().get(0).getRole().getName());

        //????????? ????????? ????????? ?????? ?????? ????????? ?????? ????????????
        if(member.getMemberRoleList().get(0).getRole().getName().equals("ROLE_TUTOR")){

            TutorInfoDTO tutorInfo = profileService.findTutorInfo(memberNo);

            //????????? ?????? ???????????? ?????? ???????????? ???????????? ?????? ?????? (NPT ??????)
            if(tutorInfo != null) {
                tutorInfo.setAddress(tutorInfo.getAddress().replace("&", " "));
            } else {
                tutorInfo = new TutorInfoDTO();
                tutorInfo.setAddress("?????????");
                tutorInfo.setAccountNumber("?????????");
                tutorInfo.setBankName("?????????");
            }
            memberDetailMap.put("tutorInfo", tutorInfo);
        }

        return memberDetailMap;
    }

    @PostMapping("/stop")
    public String registLimitedMember(@RequestParam("memberNo") String memberNo, RedirectAttributes rttr,
                                   @RequestParam("stopReasons") String stopReasons, @RequestParam("stopDate") String stopDate) {

        memberService.memberStatusStop(Integer.parseInt(memberNo), Integer.parseInt(stopReasons), Integer.parseInt(stopDate));

        rttr.addFlashAttribute("message", "??????????????? ?????????????????????.");

        return "redirect:/member/list";
    }

    @GetMapping("/play/{memberNo}")
    public String modifyLimitedMember(@PathVariable("memberNo") int memberNo, RedirectAttributes rttr) {

        memberService.memberStatusPlay(memberNo);

        rttr.addFlashAttribute("message", "??????????????? ?????????????????????.");

        return "redirect:/member/list";
    }

    @GetMapping(value = "/reasons", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<ReasonsDTO> findAllReasons() {

        return memberService.findReasonsList();
    }


    @GetMapping("/myfiles")
    public ModelAndView memberFilesPage(@AuthenticationPrincipal CustomUser loginMember, @PageableDefault Pageable pageable,
                                        ModelAndView mv) {

        Page<AttachedFileDTO> attachedFileList = memberService.findMemberAttachedFileList(loginMember.getNo(), pageable);
        PagingButtonInfo paging = Pagenation.getPagingButtonInfo(attachedFileList);

        mv.addObject("attachedFileList", attachedFileList);
        mv.addObject("paging", paging);
        mv.setViewName("/document/myfiles");

        return mv;
    }

    @GetMapping("/removefiles/{filesNo}")
    public String removeMemberFiles(@PathVariable("filesNo") String filesNo){

        memberService.removeMemberFiles(Integer.parseInt(filesNo));

        return "redirect:/member/myfiles";
    }

    @PostMapping("/secession")
    public String secessionMember(@AuthenticationPrincipal CustomUser loginMember, @RequestParam("reasonNo") int reasonNo,
                                  @RequestParam("content") String content, RedirectAttributes rttr) {

        memberService.secessionMember(loginMember.getNo(), reasonNo, content);

        rttr.addFlashAttribute("message", "????????? ???????????? ?????????????????? ???????????????.");

        return "redirect:/member/logout";
    }

    @GetMapping("/secessionCategory")
    @ResponseBody
    public List<SecessionReasonDTO> findAllSecessionCategory() {

        List<SecessionReasonDTO> secessionReasonList = memberService.findAllSecessionCategory();

        return secessionReasonList;
    }
}
