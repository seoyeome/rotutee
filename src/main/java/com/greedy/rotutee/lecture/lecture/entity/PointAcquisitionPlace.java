package com.greedy.rotutee.lecture.lecture.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * packageName      : com.greedy.rotutee.lecture.lecture.entity
 * fileName         : PointAcquisitionPlace
 * author           : SEOK
 * date             : 2022-05-09
 * description      :
 * ==========================================================
 * DATE            AUTHOR              NOTE
 * ----------------------------------------------------------
 * 2022-05-09      SEOK         최초 생성
 */
@Entity(name = "Lecture_PointAcquisitionPlace")
@Table(name = "TBL_POINT_ACQUISITION_PLACE")
public class PointAcquisitionPlace {

    @Id
    @Column(name = "ACQUISITION_PLACE_NO")
    private int placeNo;

    @Column(name = "ACQUISITION_PLACE_NAME")
    private String placeName;

    @Column(name = "ACQUISITION_POINT")
    private Integer point;

    public PointAcquisitionPlace() {
    }

    public PointAcquisitionPlace(int placeNo, String placeName, Integer point) {
        this.placeNo = placeNo;
        this.placeName = placeName;
        this.point = point;
    }

    public int getPlaceNo() {
        return placeNo;
    }

    public void setPlaceNo(int placeNo) {
        this.placeNo = placeNo;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "PointAcquisitionPlace{" +
                "placeNo=" + placeNo +
                ", placeName='" + placeName + '\'' +
                ", point=" + point +
                '}';
    }
}
