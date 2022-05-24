package com.greedy.rotutee.lecture.request.controller;

import com.greedy.rotutee.Authentication.dto.CustomUser;
import com.greedy.rotutee.common.paging.Pagenation;
import com.greedy.rotutee.common.paging.PagingButtonInfo;
import com.greedy.rotutee.lecture.request.dto.*;
import com.greedy.rotutee.lecture.request.service.LectureRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName      : com.greedy.rotutee.lecture.request.controller
 * fileName         : LectureRequestController
 * author           : SEOK
 * date             : 2022-04-25
 * description      :
 * ==========================================================
 * DATE            AUTHOR              NOTE
 * ----------------------------------------------------------
 * 2022-04-25      SEOK         최초 생성
 */
@Controller
@RequestMapping("/request")
public class LectureRequestController {

    private final LectureRequestService lectureRequestService;

    @Autowired
    public LectureRequestController(LectureRequestService lectureRequestService) {
        this.lectureRequestService = lectureRequestService;
    }

    /**
     * methodName : findMyLectureList
     * author : SEOK
     * description : 튜터가 개설 신청한 강의 목록 조회
     *
     * @Param ModelAndView mv
     * @Param CustomUser customUser
     * @Return ModelAndView
     * */
    @GetMapping("list")
    public ModelAndView findMyLectureList(ModelAndView mv, @AuthenticationPrincipal CustomUser customUser) {

        int memberNo = customUser.getNo();
        List<LectureDTO> lectureList = lectureRequestService.findLectureListBytutorNo(memberNo);

        mv.addObject("lectureList", lectureList);
        mv.setViewName("/request/myrequestlist");

        return mv;
    }

    /**
     * methodName : lectureRegistForm
     * author : SEOK
     * description : 강의 개설 신청 페이지 이동 기능
     *
     * @Return String "request/lecturerequestform"
     * */
    @GetMapping("lecture")
    public String lectureRegistForm() {

        return "request/lecturerequestform";
    }

    /**
     * methodName : registLectureOpeningApplication
     * author : SEOK
     * description : 강의 개설 신청 기능
     *
     * @Param ModelAndView mv
     * @Param LectureDTO newLecture 개설 신청할 강의 객체
     * @Param int categoryNo 강의 카테고리 번호
     * @Param MultipartFile thumbnailImg 썸네일 사진 파일
     * @Param MultipartFIle bannerImg 배너 사진 파일
     * @Param CustomUser customUser 로그인된 회원 객체
     * @Return ModelAndView mv
     * */
    @PostMapping("lecture")
    public ModelAndView registLectureOpeningApplication(ModelAndView mv, @ModelAttribute LectureDTO newLecture, @RequestParam int categoryNo
                                                                       , @RequestParam MultipartFile thumbnailImg, @RequestParam MultipartFile bannerImg
                                                                       , @AuthenticationPrincipal CustomUser customUser
                                                                       , RedirectAttributes rttr) throws IOException {

        List<MultipartFile> requestFileList = new ArrayList<>();
        requestFileList.add(thumbnailImg);
        if(!bannerImg.isEmpty()) {
            requestFileList.add(bannerImg);
        }

        newLecture.setFileList(requestFileList);

        int memberNo = customUser.getNo();
        lectureRequestService.registLectureOpeningApplication(newLecture, categoryNo, memberNo);

        rttr.addFlashAttribute("message", "강의 개설 신청을 성공하였습니다.");
        mv.setViewName("redirect:/request/list");

        return mv;
    }

    /**
     * methodName : findLectureRequestList
     * author : SEOK
     * description : 튜터가 개설 요청한 강의 목록 조회 기능 및 검색 기능
     *
     * @Param ModelAndView mv
     * @Param Pageable pageable
     * @Param String searchCondition
     * @Param String searchValue
     * @Return ModelAndView mv
     * */
    @GetMapping("lecturelist")
    public ModelAndView findLectureRequestList(ModelAndView mv , @PageableDefault Pageable pageable, @RequestParam(required = false, defaultValue = "") String searchCondition, @RequestParam(required = false, defaultValue = "") String searchValue) {

        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("searchCondition", searchCondition);
        searchMap.put("searchValue", searchValue);

        Page<LectureDTO> requestList = lectureRequestService.findAllLecture(pageable, searchMap);
        PagingButtonInfo paging = Pagenation.getPagingButtonInfo(requestList);

        mv.addObject("requestList",requestList);
        mv.addObject("paging", paging);
        mv.setViewName("/request/adminlecturerequestlist");

        return mv;
    }

    /**
     * methodName : findRequestLetureDetail
     * author : SEOK
     * description : 개설 요청 강의의 세부 내용 조회 기능
     *
     * @Param ModelAndView mv
     * @Param int lectureNo 강의 번호
     * @Return ModelAndView mv
     * */
    @GetMapping("lecturedetail")
    public ModelAndView findRequestLetureDetail(ModelAndView mv, @RequestParam int lectureNo) {

        LectureDTO lecture = lectureRequestService.findLectureByLectureNo(lectureNo);
        List<ChapterDTO> chapterList = lecture.getChapterList();

        mv.addObject("chapterList", chapterList);
        mv.addObject("lecture", lecture);
        mv.setViewName("request/lecturerequestdetail");

        return mv;
    }

    /**
     * methodName : approveRequestLecture
     * author : SEOK
     * description : 강의 개설 요청 승인 기능
     *
     * @Param ModelAndView mv
     * @Param int lectureNo 강의 번호
     * @Return ModelAndView mv
     * */
    @PostMapping("approve")
    public ModelAndView approveRequestLecture(ModelAndView mv, @RequestParam int lectureNo, RedirectAttributes rttr) {

        lectureRequestService.modifyLectureApprovalStatus(lectureNo);

        rttr.addFlashAttribute("message", "강의가 개설되었습니다.");
        mv.setViewName("redirect:/request/lecturelist");

        return mv;
    }

    /**
     * methodName : rejectRequestLecture
     * author : SEOK
     * description : 강의 개설 요청 거절 기능
     *
     * @Param int lectureNo 강의 번호
     * @Param int rejectionCategoryNo 요청 거절 카테고리 번호
     * @Return ModelAndView mv
     * */
    @GetMapping("reject")
    public ModelAndView rejectRequestLecture(ModelAndView mv, @RequestParam int lectureNo
                                                            , @RequestParam int rejectionCategoryNo
                                                            , RedirectAttributes rttr) {

        lectureRequestService.rejectLecture(lectureNo, rejectionCategoryNo);

        rttr.addFlashAttribute("message", "해당 강의를 거절하였습니다.");
        mv.setViewName("redirect:/request/lecturelist");

        return mv;
    }

}
