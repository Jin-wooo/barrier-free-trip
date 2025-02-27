package com.triply.barrierfreetrip.review.controller;

import com.triply.barrierfreetrip.ApiResponseBody;
import com.triply.barrierfreetrip.EmptyDocument;
import com.triply.barrierfreetrip.member.domain.Member;
import com.triply.barrierfreetrip.member.repository.MemberRepository;
import com.triply.barrierfreetrip.review.domain.Review;
import com.triply.barrierfreetrip.review.dto.ReviewListDto;
import com.triply.barrierfreetrip.review.dto.ReviewRequestDto;
import com.triply.barrierfreetrip.review.service.ReviewService;
import com.triply.barrierfreetrip.touristfacility.domain.TouristFacility;
import com.triply.barrierfreetrip.touristfacility.service.TouristFacilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    private final TouristFacilityService touristFacilityService;
    private final MemberRepository memberRepository;
    private final EmptyDocument emptyDocument = new EmptyDocument();

    @PostMapping("/reviews/{contentId}")
    public ApiResponseBody<?> saveReview(@PathVariable("contentId") String contentId,
                                      @RequestBody ReviewRequestDto requestData) {
        try {
            Member member = (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //Member member = memberRepository.findById(Long.valueOf(41)).get();
            Optional<TouristFacility> touristFacility = touristFacilityService.findByContentId(contentId);

            if (touristFacility.isPresent()) {
                Review review = reviewService.createReview(member, touristFacility.get(),
                        requestData.getRating(), requestData.getContent());

            }
            return ApiResponseBody.createSuccess(emptyDocument);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }

    @GetMapping("/reviews/{contentId}")
    public ApiResponseBody<?> getReview(@PathVariable("contentId") String contentId) {
        try {
            Optional<TouristFacility> touristFacility = touristFacilityService.findByContentId(contentId);
            Map<String, Object> map = new HashMap<>();

            if (touristFacility.isPresent()) {
                List<Review> reviews = touristFacility.get().getReviews();

                List<ReviewListDto> result = reviews.stream().map(rv -> new ReviewListDto(rv.getMember().getNickname(),
                        rv.getRating(), rv.getContent(), rv.getCreatedDate())).collect(Collectors.toList());

                map.put("totalCnt", result.size());
                map.put("reviews", result);

            } else {
                map.put("totalCnt", "0");
                map.put("reviews", "리뷰를 등록할 컨텐츠가 없습니다.");
            }

            return ApiResponseBody.createSuccess(map);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }
}
