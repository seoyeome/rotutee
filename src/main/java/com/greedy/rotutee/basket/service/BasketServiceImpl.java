package com.greedy.rotutee.basket.service;

import com.greedy.rotutee.basket.dto.*;
import com.greedy.rotutee.basket.entity.*;
import com.greedy.rotutee.basket.repository.*;
import com.greedy.rotutee.payment.entity.PaymentChapter;
import com.greedy.rotutee.payment.entity.PaymentClass;
import com.greedy.rotutee.payment.repository.PaymentChapterRepository;
import com.greedy.rotutee.payment.repository.PaymentClassRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName      : com.greedy.rotutee.basket.service
 * fileName         : BasketServiceImpl
 * author           : SEOK
 * date             : 2022-05-03
 * description      :
 * ==========================================================
 * DATE            AUTHOR              NOTE
 * ----------------------------------------------------------
 * 2022-05-03      SEOK         최초 생성
 */
@Service
public class BasketServiceImpl implements BasketService{

    private final ModelMapper modelMapper;
    private final ClassBasketRepository classBasketRepository;
    private final ClassBasketLectureRepository classBasketLectureRepository;
    private final ClassBasketMemberRepository classBasketMemberRepository;
    private final ClassBasketMemberInterestRepository classBasketMemberInterestRepository;
    private final ClassBasketMemberCouponBoxRepository classBasketMemberCouponBoxRepository;
    private final ClassBasketMemberLectureRespository classBasketMemberLectureRespository;
    private final ClassBasketCouponRepository ClassBasketCouponRepository;
    private final ClassBasketChapterRepository classBasketChapterRepository;
    private final ClassBasketClassRepository classBasketClassRepository;
    private final ClassBasketCumulateTimeRepository classBasketCumulateTimeRepository;
    private final PaymentClassRepository paymentClassRepository;
    private final PaymentChapterRepository paymentChapterRepository;



    @Autowired
    public BasketServiceImpl(ModelMapper modelMapper, ClassBasketRepository classBasketRepository, ClassBasketLectureRepository classBasketLectureRepository, ClassBasketMemberRepository classBasketMemberRepository, ClassBasketMemberInterestRepository classBasketMemberInterestRepository, ClassBasketMemberCouponBoxRepository classBasketMemberCouponBoxRepository, ClassBasketMemberLectureRespository classBasketMemberLectureRespository, com.greedy.rotutee.basket.repository.ClassBasketCouponRepository classBasketCouponRepository, ClassBasketChapterRepository classBasketChapterRepository, ClassBasketClassRepository classBasketClassRepository, ClassBasketCumulateTimeRepository classBasketCumulateTimeRepository, PaymentClassRepository paymentClassRepository, PaymentChapterRepository paymentChapterRepository) {
        this.modelMapper = modelMapper;
        this.classBasketRepository = classBasketRepository;
        this.classBasketLectureRepository = classBasketLectureRepository;
        this.classBasketMemberRepository = classBasketMemberRepository;
        this.classBasketMemberInterestRepository = classBasketMemberInterestRepository;
        this.classBasketMemberCouponBoxRepository = classBasketMemberCouponBoxRepository;
        this.classBasketMemberLectureRespository = classBasketMemberLectureRespository;
        ClassBasketCouponRepository = classBasketCouponRepository;
        this.classBasketChapterRepository = classBasketChapterRepository;
        this.classBasketClassRepository = classBasketClassRepository;
        this.classBasketCumulateTimeRepository = classBasketCumulateTimeRepository;
        this.paymentClassRepository = paymentClassRepository;

        this.paymentChapterRepository = paymentChapterRepository;
    }

    @Override
    public ClassBasketDTO findByLectureNoAndMemberNo(int lectureNo, int memberNo) {

        Lecture lectureEntity = classBasketLectureRepository.findByLectureNo(lectureNo);
        Member memberEntity = classBasketMemberRepository.findByNo(memberNo);

        ClassBasket basket = classBasketRepository.findByLectureAndMember(lectureEntity, memberEntity);

        if(basket != null) {

            ClassBasketDTO basketDTO = new ClassBasketDTO();
            basketDTO.setClassBasketNo(basket.getClassBasketNo());
            basketDTO.setMember(modelMapper.map(basket.getMember(), MemberDTO.class));
            basketDTO.setLecture(modelMapper.map(basketDTO.getLecture(), LectureDTO.class));

            return basketDTO;
        }

        return null;
    }

    @Override
    public List<BasketMemberCouponBoxDTO> selectCouponList(int memberNo) {

        List<BasketMemberCouponBox> basketMemberCouponBox = classBasketMemberCouponBoxRepository.findByMemberNoAndCouponStatus(memberNo, "미사용");

        basketMemberCouponBox.forEach(System.out::println);
        List<BasketMemberCouponBoxDTO> basketMemberCouponBoxDTO = new ArrayList<>();
        for(BasketMemberCouponBox basketMemberCouponBox1 :basketMemberCouponBox) {
            MemberDTO member = modelMapper.map(basketMemberCouponBox1.getMember(), MemberDTO.class);
            BasketCouponDTO basketCouponDTO = modelMapper.map(basketMemberCouponBox1.getBasketCoupon(), BasketCouponDTO.class);

            BasketMemberCouponBoxDTO basketMemberCouponBoxDTO1 = new BasketMemberCouponBoxDTO();
            basketMemberCouponBoxDTO1.setCouponBoxNo(basketMemberCouponBox1.getCouponBoxNo());
            basketMemberCouponBoxDTO1.setCouponExpirationDate(basketMemberCouponBox1.getCouponExpirationDate());
            basketMemberCouponBoxDTO1.setCouponRecevingDate(basketMemberCouponBox1.getCouponRecevingDate());
            basketMemberCouponBoxDTO1.setCoupon(basketCouponDTO);
            basketMemberCouponBoxDTO1.setMember(member);
            basketMemberCouponBoxDTO1.setCouponStatus(basketMemberCouponBox1.getCouponStatus());

            basketMemberCouponBoxDTO.add(basketMemberCouponBoxDTO1);
        }

        System.out.println("basketMemberCouponBoxDTO");
        basketMemberCouponBoxDTO.forEach(System.out::println);
        return basketMemberCouponBoxDTO;
    }

    @Override
    public MemberLectureDTO findLectureNoAndMemberNo(int lectureNo, int memberNo) {

        MemberLecture memberLecture = classBasketMemberLectureRespository.findByMemberNoAndLectureNo(memberNo, lectureNo);

        return modelMapper.map(memberLecture, MemberLectureDTO.class);
    }


    @Override
    @Transactional
    public void lectureSuccessBasket(int lectureNo, int memberNo, String couponNo) {

        //강의 없애기
        Lecture lectureEntity = classBasketLectureRepository.findByLectureNo(lectureNo);
        Member memberEntity = classBasketMemberRepository.findByNo(memberNo);

        classBasketRepository.deleteByLectureAndMember(lectureEntity, memberEntity);

        //멤버 강의 인원 추가
        MemberLectureDTO memberLecture = new MemberLectureDTO();
        memberLecture.setMemberNo(memberNo);
        memberLecture.setLectureNo(lectureNo);

        classBasketMemberLectureRespository.save(modelMapper.map(memberLecture, MemberLecture.class));

        MemberLecture MemberLecture = classBasketMemberLectureRespository.findByMemberNoAndLectureNo(memberNo, lectureNo);

        int memberLectureNo = MemberLecture.getMemberLectureNo();

        if(!couponNo.equals("undefined")) {
            //결제한 후 쿠폰 변경
            int couponNum = Integer.parseInt(couponNo);
            Date date = new Date(System.currentTimeMillis());
            BasketCoupon basketCoupon = ClassBasketCouponRepository.findById(couponNum).get();
            BasketMemberCouponBox basketMemberCouponBox = classBasketMemberCouponBoxRepository.findByBasketCouponAndMember(basketCoupon, memberEntity);

            BasketCouponDTO couponDTO = modelMapper.map(basketCoupon, BasketCouponDTO.class);
            MemberDTO memberDTO = modelMapper.map(memberEntity, MemberDTO.class);

            basketMemberCouponBox.setCouponStatus("사용");//완료
        }



        //강의 시간 insert
        List<PaymentChapter> chapterList = paymentChapterRepository.findByLectureNo(lectureNo);
        List<Integer> chapterNums = new ArrayList<>();
        System.out.println("chapterNums : " + chapterNums);
        for(int i = 0; i < chapterList.size(); i++) {
            int chapterNo = chapterList.get(i).getChapterNo();
            chapterNums.add(chapterNo);
        }
        System.out.println("chapterNums = " + chapterNums);

        /* 강의에 해당하는 챕터 번호 리스트 형태 저장 */
        /*위에서 조회한 챕터1개에 해당하는 클래스 리스트 담아주기*/
        List<Integer> classNums = new ArrayList<>();
        for(int i= 0; i < chapterNums.size(); i++) {
            int chapterNo  = chapterNums.get(i);
            System.out.println("chapterNo" +chapterNo);


            List<PaymentClass> classList = paymentClassRepository.findByChapterChapterNo(chapterNo);
            System.out.println("classList :" + classList);

            for(int j = 0; j < classList.size(); j++) {
                int classNo = classList.get(j).getClassNo();
                System.out.println("classNo :" + classNo);

                classNums.add(classNo);
            }
        }
        System.out.println(classNums);
        System.out.println("gg2");

        for(int i = 0; i < classNums.size(); i++){
            System.out.println("확인" + i);
            int classNo = classNums.get(i);
            System.out.println("classNO : "+classNo);

            CumulateTimeDTO cumulateTimeDTO = new CumulateTimeDTO();
            MemberLectureDTO memberLectureDTO = new MemberLectureDTO();
            System.out.println("memberLectureNo" +memberLectureNo);


            memberLectureDTO.setMemberLectureNo(memberLectureNo);
            ClassDTO classDTO = new ClassDTO();

            classDTO.setClassNo(classNo);

            System.out.println(classNo);

            cumulateTimeDTO.setFinishedWatchingYN("N");
            cumulateTimeDTO.setMemberLecture(memberLectureDTO);
            cumulateTimeDTO.setCumClass(classDTO);
            System.out.println("cumulateTimeDTO "+cumulateTimeDTO);

            classBasketCumulateTimeRepository.save(modelMapper.map(cumulateTimeDTO, CumulateTime.class));
        }
        System.out.println("gg3");
    }


    @Override
    @Transactional
    public void registLectureToCart(int lectureNo, int memberNo) {

        Lecture lectureEntity = classBasketLectureRepository.findByLectureNo(lectureNo);
        Member memberEntity = classBasketMemberRepository.findByNo(memberNo);

        LectureDTO lecture = modelMapper.map(lectureEntity, LectureDTO.class);
        MemberDTO member = modelMapper.map(memberEntity, MemberDTO.class);

        ClassBasketDTO cart = new ClassBasketDTO();
        cart.setLecture(lecture);
        cart.setMember(member);

        classBasketRepository.save(modelMapper.map(cart, ClassBasket.class));

        LectureCategory categoryEntity = lectureEntity.getLectureCategory();

        MemberInterest memberInterest = classBasketMemberInterestRepository.findByMemberAndCategory(memberEntity, categoryEntity);
        if(memberInterest == null) {

            int degree = 2;

            MemberInterestDTO interest = new MemberInterestDTO();
            interest.setMember(member);
            interest.setCategory(lecture.getCategory());
            interest.setInterestDegree(degree);

            classBasketMemberInterestRepository.save(modelMapper.map(interest, MemberInterest.class));
        } else {
            int increasedDegree = memberInterest.getInterestDegree() + 2;
            memberInterest.setInterestDegree(increasedDegree);
        }
    }

    @Override
    public List<ClassBasketDTO> findByMemberNo(int memberNo) {

        Member member = classBasketMemberRepository.findByNo(memberNo);

        List<ClassBasket> basketList = classBasketRepository.findByMember(member);
        List<ClassBasketDTO> basketDTOList = new ArrayList<>();

        for(ClassBasket basket : basketList) {

            MemberDTO memberDTO = modelMapper.map(basket.getMember(), MemberDTO.class);
            LectureDTO lectureDTO = modelMapper.map(basket.getLecture(), LectureDTO.class);

            ClassBasketDTO basketDTO = new ClassBasketDTO();
            basketDTO.setClassBasketNo(basket.getClassBasketNo());
            basketDTO.setMember(memberDTO);
            basketDTO.setLecture(lectureDTO);

            basketDTOList.add(basketDTO);
        }

        return basketDTOList;
    }

    @Override
    @Transactional
    public void removeOneBasket(int lectureNo, int memberNo) {

        Lecture lectureEntity = classBasketLectureRepository.findByLectureNo(lectureNo);
        Member memberEntity = classBasketMemberRepository.findByNo(memberNo);

        classBasketRepository.deleteByLectureAndMember(lectureEntity, memberEntity);

    }

}
