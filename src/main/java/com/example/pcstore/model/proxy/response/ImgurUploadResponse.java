package com.example.pcstore.model.proxy.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImgurUploadResponse {

    private float status;

    private boolean success;

    private ImgurUploadData data;
}
