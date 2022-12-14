package com.greedy.rotutee.member.member.service;

import com.greedy.rotutee.Authentication.dto.CustomUser;
import com.greedy.rotutee.member.member.dto.*;
import com.greedy.rotutee.member.member.entity.*;
import com.greedy.rotutee.member.member.entity.AttachedFile;
import com.greedy.rotutee.member.member.entity.MemberRole;
import com.greedy.rotutee.member.member.repository.*;
import com.greedy.rotutee.member.profile.dto.AttachedFileDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final LectureCategoryRepository lectureCategoryRepository;
    private final MemberInterestPartRepository memberInterestPartRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final MemberAchievementHistoryRepository memberAchievementHistoryRepository;
    private final AchievementRepository achievementRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final MemberStatusHistoryRepository memberStatusHistoryRepository;
    private final MemberStatusHistoryRepositoryQuery memberStatusHistoryRepositoryQuery;
    private final ReasonsRepository reasonsRepository;
    private final AttachedFileRepository attachedFileRepository;
    private final SecessionReasonRepository secessionReasonRepository;
    private final MemberSecessionHistoryRepository memberSecessionHistoryRepository;
    private final PointAcquisitionPlaceRepository pointAcquisitionPlaceRepository;
    private final PointHistoryRepositoryQuery pointHistoryRepositoryQuery;
    private final PointHistoryRepository pointHistoryRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public MemberService(LectureCategoryRepository lectureCategoryRepository, MemberInterestPartRepository memberInterestPartRepository, PasswordEncoder passwordEncoder, MemberRepository memberRepository, MemberRoleRepository memberRoleRepository, RoleRepository roleRepository, ModelMapper modelMapper, AchievementRepository achievementRepository, MemberAchievementRepository memberAchievementRepository, MemberAchievementHistoryRepository memberAchievementHistoryRepository, MemberStatusHistoryRepository memberStatusHistoryRepository, MemberStatusHistoryRepositoryQuery memberStatusHistoryRepositoryQuery, ReasonsRepository reasonsRepository, AttachedFileRepository attachedFileRepository, SecessionReasonRepository secessionReasonRepository, MemberSecessionHistoryRepository memberSecessionHistoryRepository, PointAcquisitionPlaceRepository pointAcquisitionPlaceRepository, PointHistoryRepositoryQuery pointHistoryRepositoryQuery, PointHistoryRepository pointHistoryRepository) {

        this.lectureCategoryRepository = lectureCategoryRepository;
        this.memberInterestPartRepository = memberInterestPartRepository;
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.memberRoleRepository = memberRoleRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.achievementRepository = achievementRepository;
        this.memberAchievementRepository = memberAchievementRepository;
        this.memberAchievementHistoryRepository = memberAchievementHistoryRepository;
        this.memberStatusHistoryRepository = memberStatusHistoryRepository;
        this.memberStatusHistoryRepositoryQuery = memberStatusHistoryRepositoryQuery;
        this.reasonsRepository = reasonsRepository;
        this.attachedFileRepository = attachedFileRepository;
        this.secessionReasonRepository = secessionReasonRepository;
        this.memberSecessionHistoryRepository = memberSecessionHistoryRepository;
        this.pointAcquisitionPlaceRepository = pointAcquisitionPlaceRepository;
        this.pointHistoryRepositoryQuery = pointHistoryRepositoryQuery;
        this.pointHistoryRepository = pointHistoryRepository;
    }

    /* ?????????????????? ????????? ?????? ????????? ????????? */
    public MemberDTO findMember(int memberNo) {

        return modelMapper.map(memberRepository.findById(memberNo).get(), MemberDTO.class);
    }

    /* ?????? ?????? ???????????? ?????? ????????? ????????? */
    public List<LectureCategoryDTO> findLectureCategoryList() {

        List<LectureCategory> lectureCategoryList =  lectureCategoryRepository.findAll();

        return lectureCategoryList.stream().map(lectureCategory ->
                modelMapper.map(lectureCategory, LectureCategoryDTO.class)).collect(Collectors.toList());
    }

    /* ????????? ????????? ????????? */
    @Transactional
    public void registMember(MemberDTO member, int[] categoryNo) {

        //???????????? ?????????
        String encodePwd = passwordEncoder.encode(member.getPwd());
        member.setPwd(encodePwd);

        memberRepository.save(modelMapper.map(member, Member.class));

        //????????? ????????? ??????
        setMemberPointHistory(member);
        //??????????????? ????????? ??????
        setMemberInterest(member, categoryNo);
        //???????????? ??????
        setMemberStatus(member);
        //?????? ??????
        setMemberRole(member);
    }

    /* ????????? ????????? ??????????????? ????????? */
    private void setMemberPointHistory(MemberDTO member) {

        Member foundMember = memberRepository.findMemberByEmail(member.getEmail());
        PointAcquisitionPlace pointAcquisitionPlace = pointAcquisitionPlaceRepository.findById(99).get();
        PointHistory pointHistory = new PointHistory();
        pointHistory.setChangePoint(pointAcquisitionPlace.getPoint());
        pointHistory.setChangeDate(new Date(System.currentTimeMillis()));
        pointHistory.setDivision("??????");
        pointHistory.setMember(foundMember);
        pointHistory.setPointAcquisitionPlace(pointAcquisitionPlace);
        pointHistory.setFinalPoint(pointAcquisitionPlace.getPoint());

        pointHistoryRepository.save(pointHistory);
    }


    /* ????????? ????????? ???????????? ????????? ????????? */
    private void setMemberInterest(MemberDTO member, int[] categoryNo) {

        Member foundMember = memberRepository.findMemberByEmail(member.getEmail());
        List<LectureCategory> lectureCategoryList = lectureCategoryRepository.findAll();

        for(LectureCategory lectureCategory : lectureCategoryList) {
            MemberInterestPart memberInterestPart = new MemberInterestPart();
            memberInterestPart.setMember(foundMember);
            memberInterestPart.setLectureCategory(lectureCategory);
            memberInterestPart.setInterestDegree(0);
            //??????????????? ????????? ?????? ??????????????? ?????? ??? ????????? ????????? ??????
            for (int i = 0; i < categoryNo.length; i++) {
                if (categoryNo[i] == memberInterestPart.getLectureCategory().getNo()) {
                    memberInterestPart.setInterestDegree(10);
                }
            }

            memberInterestPartRepository.save(memberInterestPart);
        }
    }

    /* ????????? ????????? ??????????????? ????????? */
    private void setMemberStatus(MemberDTO member) {

        Member foundMember = memberRepository.findMemberByEmail(member.getEmail());
        MemberStatusHistory memberStatusHistory = new MemberStatusHistory();
        memberStatusHistory.setMember(foundMember);
        memberStatusHistory.setStatus("??????");
        memberStatusHistory.setHistoryDate(new Date(System.currentTimeMillis()));

        memberStatusHistoryRepository.save(memberStatusHistory);
    }

    /* ????????? ????????? ??????????????? ????????? */
    private void setMemberAchievement(MemberDTO member) {

        Member foundMember = memberRepository.findMemberByEmail(member.getEmail());
        MemberAchievement memberAchievement = new MemberAchievement();
        memberAchievement.setAchievement(achievementRepository.findById(1).get());
        memberAchievement.setMember(foundMember);
        long miliseconds = System.currentTimeMillis();
        Date date = new Date(miliseconds);
        memberAchievement.setGetDat(date);
        memberAchievementRepository.save(memberAchievement);

        MemberAchievementHistory memberAchievementHistory = new MemberAchievementHistory();
        memberAchievementHistory.setMemberAchievement(memberAchievementRepository.findByMemberAndAchievement(foundMember, achievementRepository.findById(1).get()));
        memberAchievementHistory.setMember(foundMember);
        memberAchievementHistory.setChangeDate(date);

        memberAchievementHistoryRepository.save(memberAchievementHistory);
    }

    /* ????????? ????????? ??????????????? ????????? */
    private void setMemberRole(MemberDTO member) {

        MemberRole memberRole = new MemberRole();
        memberRole.setMember(memberRepository.findMemberByEmail(member.getEmail()));
        //???????????? ?????????????????? ??????
        memberRole.setRole(roleRepository.findRoleByNo(3));
        memberRoleRepository.save(memberRole);
    }

    /* ????????? ?????? ????????? ????????? */
    public boolean duplicateEmail(String checkEmail) {

        //????????? ???????????? DB??? ????????? ture ??????
        return memberRepository.findMemberByEmail(checkEmail) == null ? true : false;
    }

    /* ???????????? ???????????? ????????? */
    @Transactional
    public void findMemberPwd(MemberDTO member) {

        //????????? ???????????? ?????????
        String encodePwd = passwordEncoder.encode(member.getPwd());
        //???????????? ????????? ?????? ??????
        Member findMember = memberRepository.findMemberByEmail(member.getEmail());
        findMember.setPwd(encodePwd);
    }

    /* ???????????? ????????? ????????? */
    @Transactional
    public void modifyPassword(CustomUser loginMember, String modifyPassword) {

        //????????? ???????????? ?????????
        String encodePwd = passwordEncoder.encode(modifyPassword);
        //???????????? ????????? ?????? ??????
        Member findMember = memberRepository.findById(loginMember.getNo()).get();
        findMember.setPwd(encodePwd);
    }


    public Page<MemberDTO> findAllMember(Pageable pageable) {

        pageable = PageRequest.of(pageable.getPageNumber() <= 0? 0: pageable.getPageNumber() - 1,
                pageable.getPageSize(),
                Sort.by("registrationDate").descending());

        //?????? ?????? ?????? ??????
        return memberRepository.findAll(pageable).map(member -> modelMapper.map(member, MemberDTO.class));
    }


    public MemberStatusHistoryDTO findMemberStatus(int memberNo) {

        //????????? ????????? ?????? ?????? ?????? (=????????? ?????? ????????????)
        return modelMapper.map(memberStatusHistoryRepositoryQuery.findMemberStatus(entityManager, memberNo), MemberStatusHistoryDTO.class);
    }

    @Transactional
    public void memberStatusStop(int memberNo, int stopReasons, int stopDate) {

        long miliseconds = System.currentTimeMillis();
        Date date = new Date(miliseconds);
        //????????? ?????? (?????? + (?????? x ????????? ??? ??????))
        Date endDate = new Date(miliseconds + (long)( ( 1000 * 60 * 60 * 24 ) * stopDate ));

        //?????? ?????? ??????
        SuspensionHitory suspensionHitory = new SuspensionHitory();
        suspensionHitory.setStartDate(date);
        suspensionHitory.setEndDate(endDate);
        suspensionHitory.setReasons(reasonsRepository.findById(stopReasons).get());
        //?????? ???????????? ?????? ??????
        MemberStatusHistory memberStatusHistory = new MemberStatusHistory();
        memberStatusHistory.setStatus("??????");
        memberStatusHistory.setMember(memberRepository.findById(memberNo).get());
        memberStatusHistory.setSuspensionHitory(suspensionHitory);
        memberStatusHistory.setHistoryDate(date);
        suspensionHitory.setMemberStatusHistory(memberStatusHistory);

        memberStatusHistoryRepository.save(memberStatusHistory);

    }

    public List<ReasonsDTO> findReasonsList() {

        return reasonsRepository.findAll().stream().map(reasons -> modelMapper.map(reasons, ReasonsDTO.class)).collect(Collectors.toList());
    }

    /* ?????? ?????? ?????? ????????? */
    @Transactional
    public void memberStatusPlay(int memberNo) {

        //?????? ???????????? ?????? ??????
        Date date = new Date(System.currentTimeMillis());
        MemberStatusHistory memberStatusHistory = new MemberStatusHistory();
        memberStatusHistory.setStatus("??????");
        memberStatusHistory.setMember(memberRepository.findById(memberNo).get());
        memberStatusHistory.setHistoryDate(date);

        memberStatusHistoryRepository.save(memberStatusHistory);
    }

    public Page<AttachedFileDTO> findMemberAttachedFileList(int memberNo, Pageable pageable) {

        pageable = PageRequest.of(pageable.getPageNumber() <= 0? 0: pageable.getPageNumber() - 1,
                pageable.getPageSize(),
                Sort.by("attachedFileNo").descending());

        //??????????????? ?????? ??????????????? ????????? ??????
        String division = "??????";
        Page<AttachedFile> attachedFilesList = attachedFileRepository.findByMemberNoAndDivisionAndFileDeletionYn(memberNo, division, "N ", pageable);

        return attachedFilesList.map(attachedFile -> modelMapper.map(attachedFile, AttachedFileDTO.class));
    }

    @Transactional
    public void removeMemberFiles(int filesNo) {

        //????????? ?????? ?????? ??????
        AttachedFile attachedFile = attachedFileRepository.findById(filesNo).get();
        attachedFile.setFileDeletionYn("Y");
    }

    public Page<MemberDTO> findSearchMemberList(String searchCondition, String searchValue, Pageable pageable) {

        pageable = PageRequest.of(pageable.getPageNumber() <= 0? 0: pageable.getPageNumber() - 1,
                pageable.getPageSize(),
                Sort.by("no").descending());

        Page<Member> memberList = null;

        //?????? ????????? ?????? ?????? ??????
        if("name".equals(searchCondition)) {
            //???????????? ????????? ??????
            memberList = memberRepository.findByNameContaining(searchValue, pageable);
        } else if("email".equals(searchCondition)) {
            //???????????? ????????? ??????
            memberList = memberRepository.findByEmailContaining(searchValue, pageable);
        } else if("category".equals(searchCondition) && "??????".equals(searchValue)) {
            //????????????(??????)??? ????????? ??????
            memberList = memberRepository.findByMemberRoleListRoleNo(4, pageable);
        } else if("category".equals(searchCondition) && "??????".equals(searchValue)) {
            //????????????(??????)??? ????????? ??????
            memberList = memberRepository.findByMemberRoleListRoleNo(3, pageable);
        }

        return memberList.map(member -> modelMapper.map(member, MemberDTO.class));
    }

    @Transactional
    public void secessionMember(int memberNo, int reasonNo, String content) {

        //?????? ???????????? ?????? ??????
        MemberStatusHistory memberStatusHistory = new MemberStatusHistory();
        memberStatusHistory.setMember(memberRepository.findById(memberNo).get());
        memberStatusHistory.setStatus("??????");
        memberStatusHistory.setHistoryDate(new Date(System.currentTimeMillis()));
        //?????? ?????? ?????? ??????
        MemberSecessionHistory memberSecessionHistory = new MemberSecessionHistory();
        memberSecessionHistory.setSecessionDate(new Date(System.currentTimeMillis()));
        memberSecessionHistory.setSecessionReason(secessionReasonRepository.findById(reasonNo).get());
        memberSecessionHistory.setContent(content);
        memberSecessionHistory.setMemberStatusHistory(memberStatusHistory);

        memberSecessionHistoryRepository.save(memberSecessionHistory);
    }

    public List<SecessionReasonDTO> findAllSecessionCategory() {

        //?????? ???????????? ??????
        List<SecessionReason> secessionReasonList = secessionReasonRepository.findAll();

        return secessionReasonList.stream().map(secessionReason -> modelMapper.map(secessionReason, SecessionReasonDTO.class)).collect(Collectors.toList());
    }

}
