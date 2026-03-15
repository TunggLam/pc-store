package com.example.pcstore.repositories;

import com.example.pcstore.entity.ImgurUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgurUploadRepository extends JpaRepository<ImgurUpload, String> {
}
