package com.greedy.rotutee.study.dto;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StudyByTagDTO {

    private int StudyByTagNo;
    private StudyDTO study;
    private TagDTO tag;
}
