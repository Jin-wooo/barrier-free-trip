package com.triply.barrierfreetrip.touristfacility.repository;

import com.triply.barrierfreetrip.touristfacility.domain.TouristFacility;

import java.util.List;
import java.util.Optional;

public interface TouristFacilityRepository {
    public void save(TouristFacility touristFacility);
    public List<TouristFacility> findByCode(String contentTypeId,
                                            String areaCode,
                                            String sigunguCode);
    public List<String> findImgByContentId(String contentId);

    public Optional<TouristFacility> findByContentId(String contentId);
    public Optional<TouristFacility> findByTitle(String keyword);

    public List<TouristFacility> findNearHotelsByPos(Double userX, Double userY, double dis);
}
