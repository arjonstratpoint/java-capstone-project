package com.example.arjon.repository;

import com.example.arjon.model.Content;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentRepository  extends ListCrudRepository<Content, Integer> {

    List<Content> findAllByTitleContains(String keyword);

    @Query("select * from Content where status = :status")
    List<Content> findByStatus(@Param("status") String status);

}
