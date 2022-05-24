package com.greedy.rotutee.lecture.lecture.service;

import com.greedy.rotutee.lecture.lecture.dto.*;
import com.greedy.rotutee.lecture.lecture.entity.*;
import com.greedy.rotutee.lecture.lecture.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName      : com.greedy.rotutee.lecture.lecture.service
 * fileName         : LectureMainServiceImpl
 * author           : SEOK
 * date             : 2022-04-20
 * description      :
 * ==========================================================
 * DATE            AUTHOR              NOTE
 * ----------------------------------------------------------
 * 2022-04-20      SEOK         최초 생성
 */
@Service
public class LectureMainServiceImpl implements  LectureMainService{

    private final LectureMainRepository lectureMainRepository;
    private final ModelMapper modelMapper;
    private final ChapterRepository chapterRepository;
    private final LectureReviewMainRepository lectureReviewMainRepository;
    private final MemberLectureMainRepository memberLectureMainRepository;
    private final LectureAttachedFileRepository lectureAttachedFileRepository;
    private final LectureMemberInterestRepository lectureMemberInterestRepository;
    private final MemberRepository memberRepository;
    private final LecturePointHistoryRepository lecturePointHistoryRepository;
    private final LecturePointAcquisitionPlaceRepository lecturePointAcquisitionPlaceRepository;
    private final LectureCategoryMainRepository lectureCategoryRepository;

    @Autowired
    public LectureMainServiceImpl(LectureMainRepository lectureMainRepository, ModelMapper modelMapper, ChapterRepository chapterRepository, LectureReviewMainRepository lectureReviewMainRepository, MemberLectureMainRepository memberLectureMainRepository, LectureAttachedFileRepository lectureAttachedFileRepository, LectureMemberInterestRepository lectureMemberInterestRepository, MemberRepository memberRepository, LecturePointHistoryRepository lecturePointHistoryRepository, LecturePointAcquisitionPlaceRepository lecturePointAcquisitionPlaceRepository, LectureCategoryMainRepository lectureCategoryRepository) {
        this.lectureMainRepository = lectureMainRepository;
        this.modelMapper = modelMapper;
        this.chapterRepository = chapterRepository;
        this.lectureReviewMainRepository = lectureReviewMainRepository;
        this.memberLectureMainRepository = memberLectureMainRepository;
        this.lectureAttachedFileRepository = lectureAttachedFileRepository;
        this.lectureMemberInterestRepository = lectureMemberInterestRepository;
        this.memberRepository = memberRepository;
        this.lecturePointHistoryRepository = lecturePointHistoryRepository;
        this.lecturePointAcquisitionPlaceRepository = lecturePointAcquisitionPlaceRepository;
        this.lectureCategoryRepository = lectureCategoryRepository;
    }

    /**
     * methodName : findAllLecture
     * author : SEOK
     * description : 승인 상태의 강의와 강의의 썸네일 조회 기능
     *
     * @Return List<LectureDTO> lectureDTOList
     * */
    @Override
    public List<LectureDTO> findAllLecture() {

        String lectureApprovalStatus = "승인";

        List<Lecture> lectureList = lectureMainRepository.findBylectureApprovalStatus(lectureApprovalStatus);

        List<LectureDTO> lectureDTOList = lectureList.stream().map(lecture -> modelMapper.map(lecture, LectureDTO.class)).collect(Collectors.toList());

        for(LectureDTO lecture : lectureDTOList) {
            lecture.setSaveFileName(lectureAttachedFileRepository.findSaveFileNameBylectureNo(lecture.getLectureNo()));
        }

        return lectureDTOList;
    }

    /**
     * methodName : findApproveLectureBysearchObject
     * author : SEOK
     * description : 검색어와 검색조건을 이용해 특정 강의 조회 기능 및 썸네일 사진 조회 기능
     *
     * @Param int searchCondition 검색조건
     * @Param String searchValue 검색어
     * @Return List<LectureDTO> lectureDTOList
     * */
    @Override
    public List<LectureDTO> findApproveLectureBysearchObject(int searchCondition, String searchValue) {

        List<Lecture> lectureList = null;
        List<LectureDTO> lectureDTOList = null;

        if(searchCondition == 1) {
            lectureList = lectureMainRepository.findBylectureNameContaining(searchValue);
            lectureDTOList = lectureList.stream().map(lecture -> modelMapper.map(lecture, LectureDTO.class)).collect(Collectors.toList());

            for(LectureDTO lecture : lectureDTOList) {
                lecture.setSaveFileName(lectureAttachedFileRepository.findSaveFileNameBylectureNo(lecture.getLectureNo()));
            }

        } else if(searchCondition == 2) {
            lectureList = lectureMainRepository.findLecturesByTutorName(searchValue);
            lectureDTOList = lectureList.stream().map(lecture -> modelMapper.map(lecture, LectureDTO.class)).collect(Collectors.toList());

            for(LectureDTO lecture : lectureDTOList) {
                lecture.setSaveFileName(lectureAttachedFileRepository.findSaveFileNameBylectureNo(lecture.getLectureNo()));
            }
        }

        return lectureDTOList;
    }

    /**
     * methodName : findLectureByLectureNo
     * author : SEOK
     * description : 강의 번호로 강의 조회 및 썸네일 사진 조회 기능
     *
     * @Param int lectureNo 강의 번호
     * @Return LectureDTO lecture
     * */
    @Override
    public LectureDTO findLectureByLectureNo(int lectureNo) {

        Lecture lectureEntity = lectureMainRepository.findById(lectureNo).get();
        LectureDTO lecture = modelMapper.map(lectureEntity, LectureDTO.class);

        String division = "프로필";
        String deletion = "N ";

        AttachedFile image = lectureAttachedFileRepository.findByMemberNoAndDivisionAndFileDeletionYN(lecture.getTutor().getNo(), division, deletion);

        if(image != null) {
            lecture.getTutor().setImageName(image.getSaveAttachedFileName());
        }

        lecture.setSaveFileName(lectureAttachedFileRepository.findSaveFileNameBylectureNo(lecture.getLectureNo()));

        return lecture;
    }

    /**
     * methodName : findChapterListByLectureNo
     * author : SEOK
     * description : 강의의 챕터 목록 조회 기능
     *
     * @Param int lectureNo 강의 번호
     * @Return List<ChapterDTO>
     * */
    @Override
    public List<ChapterDTO> findChapterListByLectureNo(int lectureNo) {

        List<Chapter> chapterList = chapterRepository.findByLectureNo(lectureNo);

        return chapterList.stream().map(chapter -> modelMapper.map(chapter, ChapterDTO.class)).collect(Collectors.toList());
    }

    /**
     * methodName : findReviewListByLectureNo
     * author : SEOK
     * description : 강의 번호로 강의의 삭제 상태가 아닌 강의평 목록 조회 및 작성자 프로필 사진 조회 기능
     *
     * @Param int lectureNo 강의 번호
     * @Return List<LectureReviewDTO> lectureReviewList
     * */
    @Override
    public List<LectureReviewDTO> findReviewListByLectureNo(int lectureNo) {

        List<LectureReview> lectureReviewEntityList = lectureReviewMainRepository.findLectureReviewByLectureNoAndLectureReviewRemoveYN(lectureNo);

        List<LectureReviewDTO> lectureReviewList = lectureReviewEntityList.stream().map(lectureReview -> modelMapper.map(lectureReview, LectureReviewDTO.class)).collect(Collectors.toList());
        for(LectureReviewDTO review : lectureReviewList) {
            String division = "프로필";
            String deletion = "N ";

            AttachedFile image = lectureAttachedFileRepository.findByMemberNoAndDivisionAndFileDeletionYN(review.getWriter().getNo(), division, deletion);
            if(image != null) {
                review.getWriter().setImageName(image.getSaveAttachedFileName());
            }
        }

        return lectureReviewList;
    }

    /**
     * methodName : findMemberCountByLectureNo
     * author : SEOK
     * description : 수강중인 회원 수 조회 기능
     *
     * @Param int lectureNo 강의 번호
     * @Return int result 수강중인 회원 수
     * */
    @Override
    public int findMemberCountByLectureNo(int lectureNo) {

        int result = memberLectureMainRepository.countByLectureNo(lectureNo);

        return result;
    }

    /**
     * methodName : findGradeAverageByLectureNo
     * author : SEOK
     * description : 강의의 삭제 상태가 아닌 강의평의 평점 평균 조회 기능
     *
     * @Param int lectureNo 강의 번호
     * @Return int result 강의 평점 평균
     * */
    @Override
    public int findGradeAverageByLectureNo(int lectureNo) {

        int result = 0;

        Integer avg = lectureReviewMainRepository.findfindGradeAverageByLectureNo(lectureNo);

        if(avg != null) {
            result = avg;
        }

        return result;
    }

    /**
     * methodName : findMemberInLecture
     * author : SEOK
     * description : 회원이 강의를 수강중인지 확인 기능
     *
     * @Param memberNo 회원 번호
     * @Param lectureNo 강의 번호
     * @Return MemberLectureDTO
     * */
    @Override
    public MemberLectureDTO findMemberInLecture(int memberNo, int lectureNo) {

        MemberLecture memberLectureEntity = memberLectureMainRepository.findByMemberNoAndLectureNo(memberNo, lectureNo);

        if(memberLectureEntity == null) {
            return null;
        }

        return modelMapper.map(memberLectureEntity, MemberLectureDTO.class);
    }

    /**
     * methodName : registLectureReviewAndPoint
     * author : SEOK
     * description : 강의평 작성 기능 및 최초 작성시 포인트 추가 기능
     *
     * @Param int rating 강의 평점
     * @Param String content 강의평 내용
     * @Param int lectureNo 강의 번호
     * @Param int memberNo 회원 번호
     * */
    @Override
    @Transactional
    public void registLectureReviewAndPoint(int rating, String content, int lectureNo, int memberNo) {

        Member memberEntity = memberRepository.findById(memberNo).get();
        MemberDTO member = modelMapper.map(memberEntity, MemberDTO.class);

        List<LectureReview> reviewEntityList = lectureReviewMainRepository.findByWriterAndLectureNo(memberEntity, lectureNo);

        if(reviewEntityList.size() <= 0) {

            LectureReviewDTO lectureReview = new LectureReviewDTO();
            lectureReview.setLectureGrade(rating);
            lectureReview.setLectureReviewContent(content);
            lectureReview.setLectureReviewDate(new Date(System.currentTimeMillis()));
            lectureReview.setLectureReviewRemoveYN("N");
            lectureReview.setLectureNo(lectureNo);
            lectureReview.setWriter(member);

            lectureReviewMainRepository.save(modelMapper.map(lectureReview, LectureReview.class));

            String place = "강의평";
            PointAcquisitionPlace acquisitionPlace = lecturePointAcquisitionPlaceRepository.findByPlaceName(place);

            PointAcquisitionPlaceDTO acquisitionPlaceDTO = modelMapper.map(acquisitionPlace, PointAcquisitionPlaceDTO.class);

            List<PointHistory> historyList = lecturePointHistoryRepository.findByMemberOrderByHistoryNoDesc(memberEntity);

            if(historyList.size() > 0) {
                PointHistory recentHistory = historyList.get(0);
                int finalPoint = acquisitionPlace.getPoint() + recentHistory.getFinalPoint();
                String division = "획득";

                PointHistoryDTO newHistory = new PointHistoryDTO();
                newHistory.setChangeDate(new Date(System.currentTimeMillis()));
                newHistory.setChangePoint(acquisitionPlace.getPoint());
                newHistory.setFinalPoint(finalPoint);
                newHistory.setDivision(division);
                newHistory.setMember(member);
                newHistory.setPointAcquisitionPlace(acquisitionPlaceDTO);

                lecturePointHistoryRepository.save(modelMapper.map(newHistory, PointHistory.class));

            } else {
                String division = "획득";

                PointHistoryDTO newHistory = new PointHistoryDTO();
                newHistory.setChangeDate(new Date(System.currentTimeMillis()));
                newHistory.setChangePoint(acquisitionPlace.getPoint());
                newHistory.setFinalPoint(acquisitionPlace.getPoint());
                newHistory.setDivision(division);
                newHistory.setMember(member);
                newHistory.setPointAcquisitionPlace(acquisitionPlaceDTO);

                lecturePointHistoryRepository.save(modelMapper.map(newHistory, PointHistory.class));

            }

        } else {
            LectureReviewDTO lectureReview = new LectureReviewDTO();
            lectureReview.setLectureGrade(rating);
            lectureReview.setLectureReviewContent(content);
            lectureReview.setLectureReviewDate(new Date(System.currentTimeMillis()));
            lectureReview.setLectureReviewRemoveYN("N");
            lectureReview.setLectureNo(lectureNo);
            lectureReview.setWriter(member);

            lectureReviewMainRepository.save(modelMapper.map(lectureReview, LectureReview.class));

        }
    }

    /**
     * methodName : modifyReviewContent
     * author : SEOK
     * description : 강의평 수정 기능
     *
     * @Param int lectureReviewNo 강의평 번호
     * @Param String lectureReviewContent 수정할 강의평 내용
     * */
    @Override
    @Transactional
    public void modifyReviewContent(int lectureReviewNo, String lectureReviewContent) {

        LectureReview foundReview = lectureReviewMainRepository.findById(lectureReviewNo).get();
        foundReview.setLectureReviewContent(lectureReviewContent);

    }

    /**
     * methodName : removeReview
     * author : SEOK
     * description : 강의평 삭제 기능
     *
     * @Param int lectureReviewNo 강의평 번호
     * */
    @Override
    @Transactional
    public void removeReview(int lectureReviewNo) {

        String status = "Y";

        LectureReview foundReview = lectureReviewMainRepository.findById(lectureReviewNo).get();
        foundReview.setLectureReviewRemoveYN(status);

    }

    /**
     * methodName : registInterestDegree
     * author : SEOK
     * description : 회원 강의 카테고리 관심도 추가 기능
     *
     * @Param int no 회원 번호
     * @Param LectureCategoryDTO category 강의 카테고리 객체
     * */
    @Override
    @Transactional
    public void registInterestDegree(int no, LectureCategoryDTO category) {

        Member member = memberRepository.findById(no).get();

        LectureCategory categoryEntity = modelMapper.map(category, LectureCategory.class);

        MemberInterest memberInterest = lectureMemberInterestRepository.findByMemberAndCategory(member, categoryEntity);

        if(memberInterest == null) {
            int degree = 1;

            MemberInterestDTO interest = new MemberInterestDTO();
            interest.setMember(modelMapper.map(member, MemberDTO.class));
            interest.setCategory(category);
            interest.setInterestDegree(degree);

            lectureMemberInterestRepository.save(modelMapper.map(interest, MemberInterest.class));

        } else {
            int increasedDegree = memberInterest.getInterestDegree() + 1;
            memberInterest.setInterestDegree(increasedDegree);

        }

    }

    /**
     * methodName : findLectureByCategoryName
     * author : SEOK
     * description : 카테고리 이름으로 강의 조회 기능
     *
     * @Param String categoryName 강의 카테고리 이름
     * @Return List<LectureDTO>
     * */
    @Override
    public List<LectureDTO> findLectureByCategoryName(String categoryName) {

        LectureCategory category = lectureCategoryRepository.findByLectureCategoryName(categoryName);

        String status = "승인";

        List<Lecture> lectureList = lectureMainRepository.findByLectureCategoryAndLectureApprovalStatus(category, status);

        List<LectureDTO> lectureDTOList = lectureList.stream().map(lecture -> modelMapper.map(lecture, LectureDTO.class)).collect(Collectors.toList());

        for(LectureDTO lecture : lectureDTOList) {
            lecture.setSaveFileName(lectureAttachedFileRepository.findSaveFileNameBylectureNo(lecture.getLectureNo()));
        }

        return lectureDTOList;
    }

}
