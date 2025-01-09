package com.triply.barrierfreetrip.rental.controller;

import com.triply.barrierfreetrip.ApiResponseBody;
import com.triply.barrierfreetrip.EmptyDocument;
import com.triply.barrierfreetrip.ResponseBodyWrapper;
import com.triply.barrierfreetrip.member.domain.Member;
import com.triply.barrierfreetrip.member.repository.MemberRepository;
import com.triply.barrierfreetrip.rental.dto.RentalListDto;
import com.triply.barrierfreetrip.rental.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;
    private final MemberRepository memberRepository;
    private final EmptyDocument emptyDocument = new EmptyDocument();

    @GetMapping("/rentals/{sido}/{sigungu}")
    public ApiResponseBody<?> returnRentalServiceList(@PathVariable("sido") String sido,
                                                      @PathVariable("sigungu") String sigungu) {
        try {
            //Member member = memberRepository.findById(Long.valueOf(41)).get();
            Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<RentalListDto> rentalListDtos = rentalService.returnRentalServiceList(member, sido, sigungu);

            ResponseBodyWrapper<List<RentalListDto>> result = new ResponseBodyWrapper<>(rentalListDtos);
            return ApiResponseBody.createSuccess(result);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }
}
