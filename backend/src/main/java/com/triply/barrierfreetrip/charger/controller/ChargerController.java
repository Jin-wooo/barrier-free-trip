package com.triply.barrierfreetrip.charger.controller;


import com.triply.barrierfreetrip.ApiResponseBody;
import com.triply.barrierfreetrip.EmptyDocument;
import com.triply.barrierfreetrip.ResponseBodyWrapper;
import com.triply.barrierfreetrip.charger.dto.ChargerInfoDto;
import com.triply.barrierfreetrip.charger.dto.ChargerListDto;
import com.triply.barrierfreetrip.charger.service.ChargerService;
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
public class ChargerController {
    private final ChargerService chargerService;
    private final MemberRepository memberRepository;
    private final EmptyDocument emptyDocument = new EmptyDocument();

    @GetMapping("/chargers/{sido}/{sigungu}")
    public ApiResponseBody<?> returnChargerList(@PathVariable("sido") String sido,
                                            @PathVariable("sigungu") String sigungu) {
        try {
            //Member member = memberRepository.findById(Long.valueOf(41)).get();
            Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<ChargerListDto> chargers = chargerService.returnListDto(member, sido, sigungu);

            ResponseBodyWrapper<List<ChargerListDto>> result = new ResponseBodyWrapper<>(chargers);
            return ApiResponseBody.createSuccess(result);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }

    @GetMapping("/chargers/info/{contentId}")
    public ApiResponseBody<?> returnChargerInfo(@PathVariable("contentId") Long contentId) {
        ApiResponseBody<ChargerInfoDto> result;
        try {
            //Member member = memberRepository.findById(Long.valueOf(41)).get();
            Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ChargerInfoDto chargerInfoDto = chargerService.returnChargerInfo(member, contentId);

            return ApiResponseBody.createSuccess(chargerInfoDto);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }


    }

    @GetMapping("/near-chargers/{userX}/{userY}")
    public ApiResponseBody<?> returnNearChargers(@PathVariable("userX") double userX,
                                             @PathVariable("userY") double userY) {
        try {
            List<ChargerListDto> chargerListDtos = chargerService.returnNearChargerDto(userX, userY, 3);

            ResponseBodyWrapper<List<ChargerListDto>> result = new ResponseBodyWrapper<>(chargerListDtos);
            return ApiResponseBody.createSuccess(result);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }
}
