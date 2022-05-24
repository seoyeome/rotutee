package com.greedy.rotutee.basket.service;

import com.greedy.rotutee.basket.dto.BasketMemberCouponBoxDTO;
import com.greedy.rotutee.basket.dto.ClassBasketDTO;
import com.greedy.rotutee.basket.dto.MemberLectureDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * packageName      : com.greedy.rotutee.basket.service
 * fileName         : BasketService
 * author           : SEOK
 * date             : 2022-05-03
 * description      :
 * ==========================================================
 * DATE            AUTHOR              NOTE
 * ----------------------------------------------------------
 * 2022-05-03      SEOK         최초 생성
 */
public interface BasketService {

    void lectureSuccessBasket(int lectureNo, int memberNo, String couponNo);

    void registLectureToCart(int lectureNo, int memberNo);

    List<ClassBasketDTO> findByMemberNo(int memberNo);

    void removeOneBasket(int lectureNo, int memberNo);

    ClassBasketDTO findByLectureNoAndMemberNo(int lectureNo, int memberNo);

    List<BasketMemberCouponBoxDTO> selectCouponList(int memberNo);

    MemberLectureDTO findLectureNoAndMemberNo(int lectureNo, int memberNo);
}
