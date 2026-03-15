package com.example.pcstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "imgur_upload")
@EqualsAndHashCode(callSuper = false)
public class ImgurUpload extends BaseEntity{
    @Column(name = "status")
    private String status;

    @Column(name = "size")
    private long size;

    @Column(name = "imgur_url")
    private String imgurUrl;
}
