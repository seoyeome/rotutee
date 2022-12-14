package com.greedy.rotutee.dashboard.mypage.dto.tutee;

import lombok.*;

import java.sql.Date;

/**
 * packageName : com.greedy.rotutee.dashboard.mypage.dto
 * fileName : DashboardLectureDTO
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
public class DashboardLectureDTO {
    private int lectureNo;
    private String lectureTitle;
    private int price;
    private int tutorNo;
    private Date appliedDate;
    private int categoryNo;
    private int progress;
    private String savedFileName;

}
