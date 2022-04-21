package com.greedy.rotutee.dashboard.mypage.repository;

import com.greedy.rotutee.dashboard.mypage.entity.DashboardNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * packageName : com.greedy.rotutee.dashboard.mypage.repository
 * fileName : NoticeRepository
 * author : SeoYoung
 * date : 2022-04-19
 * description :
 * ===========================================================
 * DATE AUTHOR NOTE
 * -----------------------------------------------------------
 * 2022-04-19 SeoYoung 최초 생성
 */


public interface DashboardNoticeRepository extends JpaRepository<DashboardNotice, Integer> {
    List<DashboardNotice> findBymemberNo(int memberNo);
}
