package com.triply.barrierfreetrip.touristfacility.service;

import com.triply.barrierfreetrip.touristfacility.domain.BarrierFreeFacility;
import com.triply.barrierfreetrip.touristfacility.repository.BarrierFreeFacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BarrierFreeFacilityServiceImpl implements BarrierFreeFacilityService{
    private final BarrierFreeFacilityRepository barrierFreeFacilityRepository;
    @Override
    public Optional<BarrierFreeFacility> findByContentId(String contentId) {
        return barrierFreeFacilityRepository.findByContentId(contentId);
    }
}
