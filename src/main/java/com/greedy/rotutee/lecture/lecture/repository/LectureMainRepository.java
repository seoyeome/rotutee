package com.greedy.rotutee.lecture.lecture.repository;

import com.greedy.rotutee.lecture.lecture.entity.Lecture;
import com.greedy.rotutee.lecture.lecture.entity.LectureCategory;
import com.greedy.rotutee.lecture.lecture.service.LectureMainService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureMainRepository extends JpaRepository<Lecture, Integer> {


    List<Lecture> findBylectureApprovalStatus(String lectureApprovalStatus);

    @Query(value = "select a from Lecture_Lecture a where a.lectureName like '%' || :searchValue || '%' and a.lectureApprovalStatus = '승인'", nativeQuery = false)
    List<Lecture> findBylectureNameContaining(@Param("searchValue") String searchValue);

    @Query(value = "select a from Lecture_Lecture a where a.lectureApprovalStatus = '승인' and a.tutor.name = :searchValue", nativeQuery = false)
    List<Lecture> findLecturesByTutorName(@Param("searchValue") String searchValue);

//    List<Lecture> findByLectureCategory(LectureCategory category);

    List<Lecture> findByLectureCategoryAndLectureApprovalStatus(LectureCategory category, String status);
}
