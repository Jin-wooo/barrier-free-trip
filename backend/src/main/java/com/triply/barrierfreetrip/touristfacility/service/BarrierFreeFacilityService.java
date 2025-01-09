package com.triply.barrierfreetrip.touristfacility.service;

import com.triply.barrierfreetrip.touristfacility.domain.BarrierFreeFacility;

import java.util.List;
import java.util.Optional;

public interface BarrierFreeFacilityService {
    public Optional<BarrierFreeFacility> findByContentId(String contentId);
}
