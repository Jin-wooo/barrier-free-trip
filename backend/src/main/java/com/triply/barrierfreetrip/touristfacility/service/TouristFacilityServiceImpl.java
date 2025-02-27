package com.triply.barrierfreetrip.touristfacility.service;

import com.triply.barrierfreetrip.member.domain.Member;
import com.triply.barrierfreetrip.touristfacility.domain.BarrierFreeFacility;
import com.triply.barrierfreetrip.touristfacility.domain.TouristFacility;
import com.triply.barrierfreetrip.touristfacility.domain.TouristFacilityHeart;
import com.triply.barrierfreetrip.touristfacility.dto.TouristFacilityInfoResponseDto;
import com.triply.barrierfreetrip.touristfacility.dto.TouristFacilityListResponseDto;
import com.triply.barrierfreetrip.touristfacility.repository.TouristFacilityHeartRepository;
import com.triply.barrierfreetrip.touristfacility.repository.TouristFacilityRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TouristFacilityServiceImpl implements TouristFacilityService {
    private final TouristFacilityRepository touristFacilityRepository;
    private final TouristFacilityHeartRepository touristFacilityHeartRepository;
    private final BarrierFreeFacilityService barrierFreeFacilityService;

    @Override
    public List<TouristFacility> findByCode(String contentTypeId, String areaCode, String sigunguCode) {
        return touristFacilityRepository.findByCode(contentTypeId, areaCode, sigunguCode);
    }

    @Override
    public List<String> findImgByContentId(String contentId) {
        return touristFacilityRepository.findImgByContentId(contentId);
    }

    @Override
    public Optional<TouristFacility> findByContentId(String contentId) {
        return touristFacilityRepository.findByContentId(contentId);

    }
    @Override
    public List<TouristFacilityListResponseDto> returnListDto(Member member, String contentTypeId,
                                                              String areaCode, String sigunguCode) {
        List<TouristFacility> touristFacilities = findByCode(contentTypeId, areaCode, sigunguCode);
        List<TouristFacilityListResponseDto> result = new ArrayList<>();

        for (TouristFacility tf: touristFacilities) {
            TouristFacilityListResponseDto  dto = new TouristFacilityListResponseDto(tf.getContentId(), tf.getContentTypeId(),
                    tf.getTitle(), tf.getAddr1(), tf.getRating(), tf.getFirstimage(), tf.getTel());

            Optional<TouristFacilityHeart> heart = touristFacilityHeartRepository.findByIdsIfLikes(member, tf);
            dto.setLike(heart.isPresent());
            result.add(dto);
        }

        return result;
    }
    @Override
    public TouristFacilityInfoResponseDto returnInfoDto(Member member, String contentId) {
        List<String> imgs = findImgByContentId(contentId);
        Optional<TouristFacility> touristFacility = findByContentId(contentId);
        Optional<BarrierFreeFacility> barrierFreeFacility = barrierFreeFacilityService.findByContentId(contentId);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        TouristFacilityInfoResponseDto dto = new TouristFacilityInfoResponseDto();

        if (touristFacility.isPresent() && barrierFreeFacility.isPresent()) {
             dto = modelMapper.map(barrierFreeFacility.get(),
                    TouristFacilityInfoResponseDto.class);
            dto.setImgs(imgs);
            dto.setContentId(touristFacility.get().getContentId());
            dto.setContentTypeId(touristFacility.get().getContentTypeId());
            dto.setTitle(touristFacility.get().getTitle());
            dto.setAddr1(touristFacility.get().getAddr1());
            dto.setAddr2(touristFacility.get().getAddr2());
            dto.setOverview(touristFacility.get().getOverview());
            dto.setHomepage(touristFacility.get().getHomepage());
            dto.setTel(touristFacility.get().getTel());
            dto.setCheckInTime(touristFacility.get().getCheckInTime());
            dto.setCheckOutTime(touristFacility.get().getCheckOutTime());
            dto.setParking(touristFacility.get().getParking());
            dto.setRating(touristFacility.get().getRating());
            dto.setAreaCode(touristFacility.get().getAreaCode());
            dto.setSignguide(touristFacility.get().getSigunguCode());
            dto.setMapx(touristFacility.get().getMapx());
            dto.setMapy(touristFacility.get().getMapy());

            Optional<TouristFacilityHeart> heart = touristFacilityHeartRepository.findByIdsIfLikes(member, touristFacility.get());

            if (heart.isPresent()) {
                dto.setLike(1);
            } else {
                dto.setLike(0);
            }
        }
        return dto;
    }

    @Override
    public List<TouristFacilityListResponseDto> returnNearHotelDto(Double userX, Double userY, int dis) {
        List<TouristFacility> nearHotels = touristFacilityRepository.findNearHotelsByPos(userX, userY, dis);
        return nearHotels.stream()
                .map(tf -> new TouristFacilityListResponseDto(tf.getContentId(), tf.getContentId(),
                        tf.getTitle(), tf.getAddr1(), tf.getRating(), tf.getFirstimage(), tf.getTel()))
                .collect(Collectors.toList());

    }

    @Override
    // review update
    public void updateRating(TouristFacility touristFacility, double newRating) {
        double rating = touristFacility.getRating();
        double updatedRating = Math.round((rating + newRating) / touristFacility.getReviews().size() * 10) / 10.0;
        touristFacility.setRating(updatedRating);

        touristFacilityRepository.save(touristFacility);
    }

    @Override
    public Optional<TouristFacility> findByTitle(String keyword) {
        return touristFacilityRepository.findByTitle(keyword);
    }

}
