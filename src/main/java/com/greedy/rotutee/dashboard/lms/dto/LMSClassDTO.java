package com.greedy.rotutee.dashboard.lms.dto;

import lombok.*;

import java.sql.Date;
import java.util.List;

/**
 * packageName : com.greedy.rotutee.dashboard.lms.dto
 * fileName : LMSClassDTO
 * author : SeoYoung
 * date : 2022-04-26
 * description :
 * ===========================================================
 * DATE AUTHOR NOTE
 * -----------------------------------------------------------
 * 2022-04-26 SeoYoung 최초 생성
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LMSClassDTO {

    private int classNo;
    private String className;
    private String videoPath;
    private LMSChapterDTO chapter;
    private LMSQuizDTO quiz;
    private String whatcingStatus;
    private int timeNo;

}
