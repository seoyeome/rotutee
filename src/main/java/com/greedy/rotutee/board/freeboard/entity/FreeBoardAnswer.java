package com.greedy.rotutee.board.freeboard.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * packageName : com.greedy.rotutee.board.entity
 * fileName : FreeBoardAnswer
 * author : soobeen
 * date : 2022-04-22
 * description :
 * ===========================================================
 * DATE              AUTHOR      NOTE * -----------------------------------------------------------
 * 2022-04-22          soobeen     최초 생성
 */
@Entity(name = "FreeBoardAnswer")
@Table(name = "TBL_ANSWER")
@SequenceGenerator(
        name = "ANSWER_SEQ_GENERATOR",
        sequenceName = "ANSWER_NO",
        initialValue = 1,
        allocationSize = 1
)
public class FreeBoardAnswer {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ANSWER_SEQ_GENERATOR"
    )
    @Column(name = "ANSWER_NO")
    private int answerNo;

    @Column(name = "ANSWER_CONTENT")
    private String answerContent;

    @Column(name = "ANSWER_YN")
    private char answerYN;

    @Column(name = "ANSWER_REPORT_COUNT")
    private int answerReportCount;

    @Column(name = "ANSWER_CREATED_DATE")
    private Timestamp answerCreatedDate;

    @Column( name = "ANSWER_MODIFY_DATE")
    private Timestamp answerModifyDate;

    @Column(name = "ANSWER_DELETE_DATE")
    private Timestamp answerDeleteDate;

    @ManyToOne
    @JoinColumn(name ="BOARD_NO")
    private FreeBoard freeBoard;

    @ManyToOne
    @JoinColumn(name = "MEMBER_NO")
    private FreeBoardMember freeBoardMember;



    public FreeBoardAnswer() {
    }

    public FreeBoardAnswer(int answerNo, String answerContent, char answerYN, int answerReportCount
                           , Timestamp answerCreatedDate, Timestamp answerModifyDate, Timestamp answerDeleteDate
                           , FreeBoard freeBoard, FreeBoardMember freeBoardMember) {
        this.answerNo = answerNo;
        this.answerContent = answerContent;
        this.answerYN = answerYN;
        this.answerReportCount = answerReportCount;
        this.answerCreatedDate = answerCreatedDate;
        this.answerModifyDate = answerModifyDate;
        this.answerDeleteDate = answerDeleteDate;
        this.freeBoard = freeBoard;
        this.freeBoardMember = freeBoardMember;
    }

    public int getAnswerNo() {
        return answerNo;
    }

    public void setAnswerNo(int answerNo) {
        this.answerNo = answerNo;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public char getAnswerYN() {
        return answerYN;
    }

    public void setAnswerYN(char answerYN) {
        this.answerYN = answerYN;
    }

    public int getAnswerReportCount() {
        return answerReportCount;
    }

    public void setAnswerReportCount(int answerReportCount) {
        this.answerReportCount = answerReportCount;
    }

    public Timestamp getAnswerCreatedDate() {
        return answerCreatedDate;
    }

    public void setAnswerCreatedDate(Timestamp answerCreatedDate) {
        this.answerCreatedDate = answerCreatedDate;
    }

    public Timestamp getAnswerModifyDate() {
        return answerModifyDate;
    }

    public void setAnswerModifyDate(Timestamp answerModifyDate) {
        this.answerModifyDate = answerModifyDate;
    }

    public Timestamp getAnswerDeleteDate() {
        return answerDeleteDate;
    }

    public void setAnswerDeleteDate(Timestamp answerDeleteDate) {
        this.answerDeleteDate = answerDeleteDate;
    }

    public FreeBoard getFreeBoard() {
        return freeBoard;
    }

    public void setFreeBoard(FreeBoard freeBoard) {
        this.freeBoard = freeBoard;
    }

    public FreeBoardMember getFreeBoardMember() {
        return freeBoardMember;
    }

    public void setFreeBoardMember(FreeBoardMember freeBoardMember) {
        this.freeBoardMember = freeBoardMember;
    }

    @Override
    public String toString() {
        return "FreeBoardAnswer{" +
                "answerNo=" + answerNo +
                ", answerContent='" + answerContent + '\'' +
                ", answerYN=" + answerYN +
                ", answerReportCount=" + answerReportCount +
                ", answerCreatedDate=" + answerCreatedDate +
                ", answerModifyDate=" + answerModifyDate +
                ", answerDeleteDate=" + answerDeleteDate +
                ", freeBoard=" + freeBoard +
                ", freeBoardMember=" + freeBoardMember +
                '}';
    }
}
