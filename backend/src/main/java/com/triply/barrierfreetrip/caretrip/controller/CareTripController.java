package com.triply.barrierfreetrip.caretrip.controller;

import com.triply.barrierfreetrip.ApiResponseBody;
import com.triply.barrierfreetrip.EmptyDocument;
import com.triply.barrierfreetrip.ResponseBodyWrapper;
import com.triply.barrierfreetrip.caretrip.dto.CareTripListResponseDto;
import com.triply.barrierfreetrip.caretrip.service.CareTripService;
import com.triply.barrierfreetrip.member.domain.Member;
import com.triply.barrierfreetrip.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CareTripController {
    private final CareTripService careTripService;
    private final MemberRepository memberRepository;
    private final EmptyDocument emptyDocument = new EmptyDocument();

    /**
     * 돌봄 시설 리스트 리턴하는 함수
     * @return 돌봄 시설 리스트 리턴
     */
    @GetMapping("/care-services/{sido}/{sigungu}")
    public ApiResponseBody<?> returnCareServiceList(@PathVariable("sido") String sido,
                                                @PathVariable("sigungu") String sigungu){
        //Member member = memberRepository.findById(Long.valueOf(41)).get();
        try {
            Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<CareTripListResponseDto> careTripListResponseDtos = careTripService.returnListDto(member, sido, sigungu);

            ResponseBodyWrapper<List<CareTripListResponseDto>> result = new ResponseBodyWrapper<>(careTripListResponseDtos);
            return ApiResponseBody.createSuccess(result);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }
}
