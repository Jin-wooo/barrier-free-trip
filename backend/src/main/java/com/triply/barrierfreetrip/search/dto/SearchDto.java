package com.triply.barrierfreetrip.search.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchDto {
    private long id;
    private String title;
    private String addr;
    private String tel;
    private String firstImage;
    private double rating;
    private int type;
    private int like;
}
