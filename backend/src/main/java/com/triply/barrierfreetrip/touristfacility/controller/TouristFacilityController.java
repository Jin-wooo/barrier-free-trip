package com.triply.barrierfreetrip.touristfacility.controller;

import com.triply.barrierfreetrip.ApiResponseBody;
import com.triply.barrierfreetrip.EmptyDocument;
import com.triply.barrierfreetrip.ResponseBodyWrapper;
import com.triply.barrierfreetrip.member.domain.Member;
import com.triply.barrierfreetrip.member.repository.MemberRepository;
import com.triply.barrierfreetrip.touristfacility.domain.AreaCode;
import com.triply.barrierfreetrip.touristfacility.dto.SidoResponseDto;
import com.triply.barrierfreetrip.touristfacility.dto.SigunguResponseDto;
import com.triply.barrierfreetrip.touristfacility.dto.TouristFacilityInfoResponseDto;
import com.triply.barrierfreetrip.touristfacility.dto.TouristFacilityListResponseDto;
import com.triply.barrierfreetrip.touristfacility.repository.AreaCodeRepository;
import com.triply.barrierfreetrip.touristfacility.service.TouristFacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TouristFacilityController {
    private final TouristFacilityService touristFacilityService;
    private final MemberRepository memberRepository;
    private final AreaCodeRepository areaCodeRepository;
    private final EmptyDocument emptyDocument = new EmptyDocument();

    /**
     * 관광 시설 리스트 리턴 api
     * @param contentTypeId
     * @param areaCode
     * @param sigunguCode
     * @return
     */
    @GetMapping("/tourist-facilities/{contentTypeId}/{areaCode}/{sigunguCode}")
    public ApiResponseBody<?> returnTouristList(@PathVariable("contentTypeId") String contentTypeId,
                                            @PathVariable("areaCode") String areaCode,
                                            @PathVariable("sigunguCode") String sigunguCode
                                            ) {
        try {
            Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //Member member = memberRepository.findById(Long.valueOf(41)).get();
            List<TouristFacilityListResponseDto> touristFacilityListResponseDtos =
                    touristFacilityService.returnListDto(member, contentTypeId, areaCode, sigunguCode);

            ResponseBodyWrapper<List<TouristFacilityListResponseDto>> result = new ResponseBodyWrapper<>(touristFacilityListResponseDtos);
            return ApiResponseBody.createSuccess(result);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }

    @GetMapping("/tourist-facilities/{contentId}")
    public ApiResponseBody<?> returnTouristInfo(@PathVariable("contentId") String contentId) {
        try {
            Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //Member member = memberRepository.findById(Long.valueOf(41)).get();
            TouristFacilityInfoResponseDto touristFacilityInfoResponseDto = touristFacilityService.returnInfoDto(member, contentId);

            return ApiResponseBody.createSuccess(touristFacilityInfoResponseDto);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }

    @GetMapping("/near-hotels/{userX}/{userY}")
    public ApiResponseBody<?> returnNearHotels(@PathVariable("userX") Double userX,
                                           @PathVariable("userY") Double userY) {
        try {
            List<TouristFacilityListResponseDto> touristFacilityListResponseDto = touristFacilityService.returnNearHotelDto(userX, userY, 3);

            ResponseBodyWrapper<List<TouristFacilityListResponseDto>> result = new ResponseBodyWrapper<>(touristFacilityListResponseDto);
            return ApiResponseBody.createSuccess(result);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }

    @GetMapping("/sido")
    public ApiResponseBody<?> returnSidoCode() {
        try {
            List<AreaCode> sidoCodes = areaCodeRepository.getSidoCode();
            List<SidoResponseDto> sidoResponseDto = sidoCodes.stream().map(sc -> new SidoResponseDto(sc.getAreaCodeId().getAreaCd()
                    , sc.getCdNm())).collect(Collectors.toList());

            ResponseBodyWrapper<List<SidoResponseDto>> result = new ResponseBodyWrapper<>(sidoResponseDto);
            return ApiResponseBody.createSuccess(result);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }

    @GetMapping("/sido/{sidoCode}")
    public ApiResponseBody<?> returnSigunguCode(@PathVariable("sidoCode") String sidoCode) {
        try {
            List<AreaCode> sigunguCodes = areaCodeRepository.findByAreaCode(sidoCode);
            List<SigunguResponseDto> sigunguResponseDto = sigunguCodes.stream().map(sc -> new SigunguResponseDto(sc.getAreaCodeId().getSigunguCd()
                    , sc.getCdNm())).collect(Collectors.toList());

            ResponseBodyWrapper<List<SigunguResponseDto>> result = new ResponseBodyWrapper<>(sigunguResponseDto);
            return ApiResponseBody.createSuccess(result);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }
}
