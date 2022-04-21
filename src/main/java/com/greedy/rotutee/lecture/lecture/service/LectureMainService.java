package com.greedy.rotutee.lecture.lecture.service;

import com.greedy.rotutee.lecture.lecture.dto.*;

import java.util.List;

public interface LectureMainService {
    List<LectureDTO> findAllLecture();

    List<LectureDTO> findApproveLectureBysearchObject(int searchCondition, String searchValue);

    LectureDTO findLectureByLectureNo(int lectureNo);

    List<ChapterDTO> findChapterListByLectureNo(int lectureNo);

    List<LectureReviewDTO> findReviewListByLectureNo(int lectureNo);

    int findMemberCountByLectureNo(int lectureNo);

    int findGradeAverageByLectureNo(int lectureNo);

    MemberLectureDTO findMemberInLecture(int memberNo, int lectureNo);

    void registLectureReview(int rating, String content, int lectureNo, int memberNo);
}
