package com.example.arjon.repository;

import com.example.arjon.model.Content;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContentRepository  extends ListCrudRepository<Content, Integer> {

    @Query("select * from Content where user_id = :userId")
    List<Content> findAll(@Param("userId") Integer userId);

    @Query("select * from Content where user_id = :userId and id = :contentId")
    Optional<Content> findById(@Param("userId") Integer userId, @Param("contentId") Integer contentId);

    @Query("select * from Content where user_id = :userId and title like :keyword")
    List<Content> findAllByTitleContains(@Param("userId") Integer userId, @Param("keyword") String keyword);

    @Query("select * from Content where user_id = :userId and status = :status")
    List<Content> findByStatus(@Param("userId") Integer userId, @Param("status") String status);

}
