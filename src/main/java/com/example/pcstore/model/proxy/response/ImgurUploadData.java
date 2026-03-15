package com.example.pcstore.model.proxy.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImgurUploadData {

    private String id;

    @JsonProperty("deletehash")
    private String deleteHash;

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("account_url")
    private String accountUrl;

    @JsonProperty("ad_type")
    private String adType;

    @JsonProperty("ad_url")
    private String adUrl;

    private String title;

    private String description;

    private String name;

    private String type;

    private int width;

    private int height;

    private int size;

    private int views;

    private String section;

    private String vote;

    private float bandwidth;

    private boolean animated;

    private boolean favorite;

    @JsonProperty("in_gallery")
    private boolean inGallery;

    @JsonProperty("in_most_viral")
    private boolean inMostViral;

    @JsonProperty("has_sound")
    private boolean hasSound;

    @JsonProperty("is_ad")
    private boolean isAd;

    private String nsfw;

    private String link;

    private List<?> tags;

    private float datetime;

    private String mp4;

    private String hls;

}
