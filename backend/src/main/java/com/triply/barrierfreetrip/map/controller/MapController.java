package com.triply.barrierfreetrip.map.controller;


import com.triply.barrierfreetrip.ApiResponseBody;
import com.triply.barrierfreetrip.EmptyDocument;
import com.triply.barrierfreetrip.map.domain.NearServiceInfo;
import com.triply.barrierfreetrip.map.service.MapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MapController {
    private final MapService mapService;
    private final EmptyDocument emptyDocument = new EmptyDocument();

    @GetMapping("/map/near-service/{startX}/{startY}/{endX}/{endY}")
    public ApiResponseBody<?> returnNearService(@PathVariable("startX") double startX,
                                             @PathVariable("startY") double startY,
                                             @PathVariable("endX") double endX,
                                             @PathVariable("endY") double endY) {
        try {
            int dis = 3;
            List<NearServiceInfo> nearServiceInfos = mapService.returnNearServiceInfos(startX, startY, endX, endY, dis);

            return ApiResponseBody.createSuccess(nearServiceInfos);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }
}
