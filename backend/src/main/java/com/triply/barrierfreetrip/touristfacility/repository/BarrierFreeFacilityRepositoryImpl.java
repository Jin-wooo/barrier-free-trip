package com.triply.barrierfreetrip.touristfacility.repository;

import com.triply.barrierfreetrip.touristfacility.domain.BarrierFreeFacility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BarrierFreeFacilityRepositoryImpl implements BarrierFreeFacilityRepository{
    private final EntityManager em;
    @Override
    public Optional<BarrierFreeFacility> findByContentId(String contentId) {
        List<BarrierFreeFacility> barrierFreeFacilities = em.createQuery("select bff from BarrierFreeFacility bff " +
                        "where bff.contentId=:contentIds", BarrierFreeFacility.class)
                .setParameter("contentIds", contentId)
                .getResultList();

        return barrierFreeFacilities.stream().findAny();
    }
}
