package com.greedy.rotutee.main.service;

import com.greedy.rotutee.main.dto.AttachedFileDTO;
import com.greedy.rotutee.main.dto.LectureDTO;
import com.greedy.rotutee.main.entity.*;
import com.greedy.rotutee.main.repository.MainAttachedFileRepository;
import com.greedy.rotutee.main.repository.MainLectureRepository;
import com.greedy.rotutee.main.repository.MainMemberInterestRepository;
import com.greedy.rotutee.main.repository.MainMemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName      : com.greedy.rotutee.main.model.service
 * fileName         : MainServiceImpl
 * author           : SEOK
 * date             : 2022-05-04
 * description      :
 * ==========================================================
 * DATE            AUTHOR              NOTE
 * ----------------------------------------------------------
 * 2022-05-04      SEOK         최초 생성
 */
@Service
public class MainServiceImpl implements MainService{

    private final ModelMapper modelMapper;
    private final MainLectureRepository mainLectureRepository;
    private final MainMemberInterestRepository mainMemberInterestRepository;
    private final MainMemberRepository mainMemberRepository;
    private final MainAttachedFileRepository mainAttachedFileRepository;

    @Autowired
    public MainServiceImpl(ModelMapper modelMapper, MainLectureRepository mainLectureRepository, MainMemberInterestRepository mainMemberInterestRepository, MainMemberRepository mainMemberRepository, MainAttachedFileRepository mainAttachedFileRepository) {
        this.modelMapper = modelMapper;
        this.mainLectureRepository = mainLectureRepository;
        this.mainMemberInterestRepository = mainMemberInterestRepository;
        this.mainMemberRepository = mainMemberRepository;
        this.mainAttachedFileRepository = mainAttachedFileRepository;
    }

    /**
     * methodName : findRecentLectureList
     * author : SEOK
     * description : 개설 날짜를 최신순으로 강의 목록 조회 기능
     *
     * @Return List<LectureDTO>
     * */
    @Override
    public List<LectureDTO> findRecentLectureList() {

        List<Lecture> recentLectureList = mainLectureRepository.findRecentLectureList();

        return recentLectureList.stream().map(lecture -> modelMapper.map(lecture, LectureDTO.class)).collect(Collectors.toList());
    }

    /**
     * methodName : findPopularLectureList
     * author : SEOK
     * description : 강의 평점이 높은 순으로 강의 목록 조회
     *
     * @Return List<LectureDTO>
     * */
    @Override
    public List<LectureDTO> findPopularLectureList() {

        List<Lecture> popularLectureList = mainLectureRepository.findPopularLectureList();

        return popularLectureList.stream().map(lecture -> modelMapper.map(lecture, LectureDTO.class)).collect(Collectors.toList());
    }

    /**
     * methodName : findBannerListByMemberNo
     * author : SEOK
     * description : 관심도가 높은 강의의 배너 목록 조회
     *
     * @Param int no 회원 번호
     * @Return List<AttachedFileDTO> fileDTOList
     * */
    @Override
    public List<AttachedFileDTO> findBannerListByMemberNo(int no) {

        Member member = mainMemberRepository.findById(no).get();

        List<MemberInterest> memberInterestList = mainMemberInterestRepository.findByMemberOrderByInterestDegreeDesc(member);

        if(memberInterestList != null && memberInterestList.size() > 0) {
            LectureCategory interestCategory = memberInterestList.get(0).getCategory();

            List<AttachedFile> fileList = mainAttachedFileRepository.findByCategoryNo(interestCategory.getLectureCategoryNo());
            if (fileList != null) {


                List<AttachedFileDTO> fileDTOList = fileList.stream().map(file -> modelMapper.map(file, AttachedFileDTO.class)).collect(Collectors.toList());
//                for(AttachedFile file : fileList) {
//
//                    LectureDTO lectureDTO = modelMapper.map(file.getLecture(), LectureDTO.class);
//
//                    AttachedFileDTO fileDTO = new AttachedFileDTO();
//                    fileDTO.setAttachedFileNo(file.getAttachedFileNo());
//                    fileDTO.setOriginalAttachedFileName(file.getOriginalAttachedFileName());
//                    fileDTO.setSaveAttachedFileName(file.getSaveAttachedFileName());
//                    fileDTO.setThumbnailFileName(file.getThumbnailFileName());
//                    fileDTO.setStorageFile(file.getStorageFile());
//                    fileDTO.setThumbnailFilePath(file.getThumbnailFilePath());
//                    fileDTO.setDivision(file.getDivision());
//                    fileDTO.setFileDeletionYN(file.getFileDeletionYN());
//                    fileDTO.setLecture(lectureDTO);
//
//                    fileDTOList.add(fileDTO);
//                }

                return fileDTOList;
            }

        } else {

            return null;
        }

        return null;
    }

    /**
     * methodName : findRecentBannerList
     * author : SEOK
     * description : 로그인되어 있지 않거나 관심도가 높은 카테고리의 강의 배너가 없는 경우 배너를 가진 강의를
     *               개설일 최신순으로 조회하는 기능
     *
     * @Return List<AttachedFileDTO> bannerDTOList
     * */
    @Override
    public List<AttachedFileDTO> findRecentBannerList() {

        List<AttachedFile> bannerList = mainAttachedFileRepository.findByRecentLectureBanner();

        List<AttachedFileDTO> bannerDTOList = new ArrayList<>();
//        for(AttachedFile banner : bannerList) {
//
//            LectureDTO lectureDTO = modelMapper.map(banner.getLecture(), LectureDTO.class);
//
//            AttachedFileDTO fileDTO = new AttachedFileDTO();
//            fileDTO.setAttachedFileNo(banner.getAttachedFileNo());
//            fileDTO.setOriginalAttachedFileName(banner.getOriginalAttachedFileName());
//            fileDTO.setSaveAttachedFileName(banner.getSaveAttachedFileName());
//            fileDTO.setThumbnailFileName(banner.getThumbnailFileName());
//            fileDTO.setStorageFile(banner.getStorageFile());
//            fileDTO.setThumbnailFilePath(banner.getThumbnailFilePath());
//            fileDTO.setDivision(banner.getDivision());
//            fileDTO.setFileDeletionYN(banner.getFileDeletionYN());
//            fileDTO.setLecture(lectureDTO);
//
//            bannerDTOList.add(fileDTO);
//        }
            bannerDTOList = bannerList.stream().map(banner -> modelMapper.map(banner, AttachedFileDTO.class)).collect(Collectors.toList());
        return bannerDTOList;
    }
}
