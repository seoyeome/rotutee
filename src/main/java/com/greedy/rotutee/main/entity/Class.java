package com.greedy.rotutee.main.entity;

import javax.persistence.*;
import java.util.List;

@Entity(name = "Main_Class")
@Table(name = "TBL_CLASS")
@SequenceGenerator(
        name = "REQUEST_CLASS_SEQ_GENERATOR",
        sequenceName = "CLASS_NO",
        initialValue = 1,
        allocationSize = 1
)
public class Class {

    @Id
    @Column(name = "CLASS_NO")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "REQUEST_CLASS_SEQ_GENERATOR"
    )
    private int classNo;

    @Column(name = "CLASS_NAME")
    private String className;

    @Column(name = "VIDEO_PATH")
    private String videoPath;

    @JoinColumn(name = "CHAPTER_NO")
    @ManyToOne(fetch = FetchType.LAZY)
    private Chapter chapter;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.PERSIST)
    private List<Quiz> quizList;

    @OneToMany(mappedBy = "classes", cascade = CascadeType.PERSIST)
    private List<AttachedFile> videoList;

    public Class() {
    }

    public Class(int classNo, String className, String videoPath, Chapter chapter, List<Quiz> quizList, List<AttachedFile> videoList) {
        this.classNo = classNo;
        this.className = className;
        this.videoPath = videoPath;
        this.chapter = chapter;
        this.quizList = quizList;
        this.videoList = videoList;
    }

    public int getClassNo() {
        return classNo;
    }

    public void setClassNo(int classNo) {
        this.classNo = classNo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public List<Quiz> getQuizList() {
        return quizList;
    }

    public void setQuizList(List<Quiz> quizList) {
        this.quizList = quizList;
    }

    public List<AttachedFile> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<AttachedFile> videoList) {
        this.videoList = videoList;
    }

    @Override
    public String toString() {
        return "Class{" +
                "classNo=" + classNo +
                ", className='" + className + '\'' +
                ", videoPath='" + videoPath + '\'' +
                ", chapter=" + chapter +
                '}';
    }
}
