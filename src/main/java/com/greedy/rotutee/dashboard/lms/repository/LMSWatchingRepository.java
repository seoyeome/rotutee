package com.greedy.rotutee.dashboard.lms.repository;

import com.greedy.rotutee.dashboard.lms.dto.LMSLatelyViewDTO;
import com.greedy.rotutee.dashboard.lms.entity.LMSLatelyViewClass;
import com.greedy.rotutee.dashboard.mypage.entity.DashboardLectureWatch;
import com.greedy.rotutee.dashboard.mypage.entity.MyPageMemberLecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * packageName : com.greedy.rotutee.dashboard.lms.repository
 * fileName : WatchingRepository
 * author : SeoYoung
 * date : 2022-04-26
 * description :
 * ===========================================================
 * DATE AUTHOR NOTE
 * -----------------------------------------------------------
 * 2022-04-26 SeoYoung 최초 생성
 */
public interface LMSWatchingRepository extends JpaRepository<LMSLatelyViewClass, Integer> {

}
