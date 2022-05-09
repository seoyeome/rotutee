package com.greedy.rotutee.dashboard.lms.repository;

import com.greedy.rotutee.dashboard.lms.entity.LMSNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * packageName : com.greedy.rotutee.dashboard.lms.repository
 * fileName : LMSNoticeRepository
 * author : SeoYoung
 * date : 2022-05-09
 * description :
 * ===========================================================
 * DATE AUTHOR NOTE
 * -----------------------------------------------------------
 * 2022-05-09 SeoYoung 최초 생성
 */
public interface LMSNoticeRepository extends JpaRepository<LMSNotice, Integer> {
    Page<LMSNotice> findAllByTitleContaining(String searchValue, Pageable pageable);

    Page<LMSNotice> findByCategoryNo(int categoryNo, Pageable pageable);

    Page<LMSNotice> findByCategoryNoAndTitleContaining(int categoryNo, String searchValue, Pageable pageable);

    List<LMSNotice> findByCategoryNo(int categoryNo);
}