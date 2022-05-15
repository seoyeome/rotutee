package com.greedy.rotutee.Authentication.service;

import com.greedy.rotutee.Authentication.dto.CustomUser;

import com.greedy.rotutee.member.member.dto.MemberDTO;
import com.greedy.rotutee.member.member.dto.MemberRoleDTO;
import com.greedy.rotutee.member.member.entity.LoginHistory;
import com.greedy.rotutee.member.member.entity.Member;
import com.greedy.rotutee.member.member.entity.MemberStatusHistory;
import com.greedy.rotutee.member.member.entity.RoleMenuUrl;
import com.greedy.rotutee.member.member.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final RoleMenuUrlRepository roleMenuUrlRepository;
    private final ModelMapper modelMapper;
    private final MemberStatusHistoryRepositoryQuery memberStatusHistoryRepositoryQuery;
    private final LoginHistoryRepositoryQuery loginHistoryRepositoryQuery;
    private final LoginHistoryRepository loginHistoryRepository;
    private final MemberStatusHistoryRepository memberStatusHistoryRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public AuthenticationServiceImpl(MemberRepository memberRepository, MemberRoleRepository memberRoleRepository, RoleMenuUrlRepository roleMenuUrlRepository, ModelMapper modelMapper, MemberStatusHistoryRepositoryQuery memberStatusHistoryRepositoryQuery, LoginHistoryRepositoryQuery loginHistoryRepositoryQuery, LoginHistoryRepository loginHistoryRepository, MemberStatusHistoryRepository memberStatusHistoryRepository) {
        this.memberRepository = memberRepository;
        this.memberRoleRepository = memberRoleRepository;
        this.roleMenuUrlRepository = roleMenuUrlRepository;
        this.modelMapper = modelMapper;
        this.memberStatusHistoryRepositoryQuery = memberStatusHistoryRepositoryQuery;
        this.loginHistoryRepositoryQuery = loginHistoryRepositoryQuery;
        this.loginHistoryRepository = loginHistoryRepository;
        this.memberStatusHistoryRepository = memberStatusHistoryRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //로그인 유저 조회
        Member member = memberRepository.findMemberByEmail(username);
        //조회 결과가 없을 시
        if(member == null) {
            throw new UsernameNotFoundException("회원정보가 존재하지 않습니다.");
        }

        //회원의 활동상태 조회
        MemberStatusHistory memberStatusHistory = memberStatusHistoryRepositoryQuery.findMemberStatus(entityManager, member.getNo());
        //회원의 활동 상태가 정지일 경우
        if(memberStatusHistory.getStatus().equals("정지")) {
            //현재일과 정지일을 비교하여 Exception 발생
            checkStatus(member, memberStatusHistory);
        }
        //회원 상태가 탈퇴일 경우 Exception 발생
        else if(memberStatusHistory.getStatus().equals("탈퇴")) {
            throw new LockedException("회원정보가 존재하지 않습니다.");
        }

        //로그인 이력을 확인해 최초 로그인시 룰렛 기회 + 1
        checkLogin(member);
        //로그인 이력 추가
        setLogin(member);

        //권한 설정
        MemberDTO loginMember = modelMapper.map(member, MemberDTO.class);
        List<MemberRoleDTO> memberRoleList = loginMember.getMemberRoleList();
        List<GrantedAuthority> authorities = new ArrayList<>();
        memberRoleList.forEach(memberRole -> authorities.add(new SimpleGrantedAuthority(memberRole.getRole().getName())));
        //유저정보에 저장
        return new CustomUser(loginMember, authorities);
    }

    @Transactional
    public void checkStatus(Member member, MemberStatusHistory memberStatusHistory) {
        //현재일과 정지일을 구함
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        String endDate = simpleDateFormat.format(memberStatusHistory.getSuspensionHitory().getEndDate());
        String today = todayDate.replace("-", "");
        String endDay = endDate.replace("-", "");
        //현재일과 정지일을 비교해 정지일이 지났으면 활동상태 변경
        if(Integer.parseInt(today) >= Integer.parseInt(endDay)) {
            MemberStatusHistory playMemberStatusHistory = new MemberStatusHistory();
            playMemberStatusHistory.setMember(member);
            playMemberStatusHistory.setStatus("활동");
            playMemberStatusHistory.setHistoryDate(new Date(System.currentTimeMillis()));

            memberStatusHistoryRepository.save(playMemberStatusHistory);
        }
        //정지일이 지나지 않았다면 Exception 발생
        else {
            throw new LockedException("정지된 회원입니다. 해지일은 " + endDate + " 입니다.\n해지일 이후로 로그인을 시도하시면 해지됩니다.");
        }
    }

    
    @Transactional
    public void checkLogin(Member member) {

        Date today = new Date(System.currentTimeMillis());
        List<LoginHistory> loginHistory = loginHistoryRepositoryQuery.findMemberLoginHistory(entityManager, member.getNo());

        if(loginHistory.size() == 0) {
            member.setRouletteChance(member.getRouletteChance() + 1);
            memberRepository.save(member);

            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String todayDate = simpleDateFormat.format(today);
        String loginDate = simpleDateFormat.format(loginHistory.get(0).getLoginDate());

        if(todayDate.equals(loginDate) || todayDate == loginDate) {
        } else {
            member.setRouletteChance(member.getRouletteChance() + 1);
            System.out.println(member.getRouletteChance());

            memberRepository.save(member);
        }
    }

    @Transactional
    public void setLogin(Member member) {

        LoginHistory newLogin = new LoginHistory();
        newLogin.setLoginDate(new Date(System.currentTimeMillis()));
        newLogin.setMember(member);
        newLogin.setLoginIp("123");

        loginHistoryRepository.save(newLogin);
    }

    @Override
    @Transactional
    public Map<String, List<String>> getPermitListMap() {

        Map<String, List<String>> permitListMap = new HashMap<>();
        List<String> adminPermitList = new ArrayList<>();
        List<String> subAdminPermitList = new ArrayList<>();
        List<String> tuteePermitList = new ArrayList<>();
        List<String> tutorPermitList = new ArrayList<>();
        List<String> memberPermitList = new ArrayList<>();
//
        List<RoleMenuUrl> adminRoleList = roleMenuUrlRepository.findByRoleNo(1);
        List<RoleMenuUrl> subAdminRoleList = roleMenuUrlRepository.findByRoleNo(2);
        List<RoleMenuUrl> tuteeRoleList = roleMenuUrlRepository.findByRoleNo(3);
        List<RoleMenuUrl> tutorRoleList = roleMenuUrlRepository.findByRoleNo(4);
        List<RoleMenuUrl> memberRoleList = roleMenuUrlRepository.findByRoleNo(5);

        for(int i = 0; i < adminRoleList.size(); i++) {

            adminPermitList.add(adminRoleList.get(i).getMenu().getUrl());
        }
        for(int i = 0; i < subAdminRoleList.size(); i++) {

            subAdminPermitList.add(subAdminRoleList.get(i).getMenu().getUrl());
        }
        for(int i = 0; i < tuteeRoleList.size(); i++) {
            tuteePermitList.add(tuteeRoleList.get(i).getMenu().getUrl());
        }
        for(int i = 0; i < tutorRoleList.size(); i++) {
            tutorPermitList.add(tutorRoleList.get(i).getMenu().getUrl());
        }
        for(int i = 0; i < memberRoleList.size(); i++) {
            memberPermitList.add(memberRoleList.get(i).getMenu().getUrl());
        }

        permitListMap.put("adminPermitList", adminPermitList);
        permitListMap.put("subAdminPermitList", subAdminPermitList);
        permitListMap.put("tuteePermitList", tuteePermitList);
        permitListMap.put("tutorPermitList", tutorPermitList);
        permitListMap.put("memberPermitList", memberPermitList);

        return permitListMap;
    }
}
