package com.triply.barrierfreetrip.search.controller;

import com.triply.barrierfreetrip.ApiResponseBody;
import com.triply.barrierfreetrip.EmptyDocument;
import com.triply.barrierfreetrip.ResponseBodyWrapper;
import com.triply.barrierfreetrip.search.dto.SearchDto;
import com.triply.barrierfreetrip.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    private final EmptyDocument emptyDocument = new EmptyDocument();

    @GetMapping("/search/{keyword}")
    public ApiResponseBody<?> search(@PathVariable("keyword") String keyword) {
        try {
            List<SearchDto> searchDtos = searchService.search(keyword);

            ResponseBodyWrapper<List<SearchDto>> result = new ResponseBodyWrapper<>(searchDtos);
            return ApiResponseBody.createSuccess(result);
        } catch (Exception e) {
            return ApiResponseBody.createFail(emptyDocument, e.getMessage());
        }
    }
}
