package com.greedy.rotutee.basket.dto;

import com.greedy.rotutee.basket.entity.BasketCoupon;
import com.greedy.rotutee.basket.entity.Lecture;
import com.greedy.rotutee.basket.entity.Member;

/**
 * packageName      : com.greedy.rotutee.basket.dto
 * fileName         : ClassBasketDTO
 * author           : SEOK
 * date             : 2022-05-03
 * description      :
 * ==========================================================
 * DATE            AUTHOR              NOTE
 * ----------------------------------------------------------
 * 2022-05-03      SEOK         최초 생성
 */
public class ClassBasketDTO {

    private int classBasketNo;
    private MemberDTO member;
    private LectureDTO lecture;
    private BasketCouponDTO memberCoupon;

    public ClassBasketDTO() {
    }

    public BasketCouponDTO getMemberCoupon() {
        return memberCoupon;
    }

    public void setMemberCoupon(BasketCouponDTO memberCoupon) {
        this.memberCoupon = memberCoupon;
    }

    public ClassBasketDTO(int classBasketNo, MemberDTO member, LectureDTO lecture, BasketCouponDTO memberCoupon) {
        this.classBasketNo = classBasketNo;
        this.member = member;
        this.lecture = lecture;
        this.memberCoupon = memberCoupon;
    }

    public int getClassBasketNo() {
        return classBasketNo;
    }

    public void setClassBasketNo(int classBasketNo) {
        this.classBasketNo = classBasketNo;
    }

    public MemberDTO getMember() {
        return member;
    }

    public void setMember(MemberDTO member) {
        this.member = member;
    }

    public LectureDTO getLecture() {
        return lecture;
    }

    public void setLecture(LectureDTO lecture) {
        this.lecture = lecture;
    }

    @Override
    public String toString() {
        return "ClassBasketDTO{" +
                "classBasketNo=" + classBasketNo +
                ", member=" + member +
                ", lecture=" + lecture +
                ", memberCoupon=" + memberCoupon +
                '}';
    }
}
