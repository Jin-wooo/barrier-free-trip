package com.triply.barrierfreetrip.touristfacility.repository;

import com.triply.barrierfreetrip.touristfacility.domain.BarrierFreeFacility;

import java.util.List;
import java.util.Optional;

public interface BarrierFreeFacilityRepository {
    public Optional<BarrierFreeFacility> findByContentId(String contentId);
}
