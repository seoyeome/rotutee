package com.greedy.rotutee.dashboard.mypage.dto;



import lombok.*;

import java.util.List;
/**
 * packageName : com.greedy.rotutee.dashboard.mypage.dto
 * fileName : MypageDashboardDTO
 * author : SeoYoung
 * date : 2022-04-19
 * description :
 * ===========================================================
 * DATE AUTHOR NOTE
 * -----------------------------------------------------------
 * 2022-04-19 SeoYoung 최초 생성
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class MypageDashboardDTO {

    private int meberNo;
    private String nickname;
    private String introduction;
    private List<DashboardBoardDTO> boardList;
    private List<DashboardBasketDTO> basketList;
    private List<DashboardLectureDTO> latelyLectureList;
    private List<DashboardLectureDTO> completedLectureList;

}
