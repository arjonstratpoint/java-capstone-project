package com.example.arjon.repository;

import com.example.arjon.model.ForgotPassword;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ForgotPasswordRepository extends CrudRepository<ForgotPassword, Integer> {
    @Query("select * from forgot_password where user_id = :userId and is_valid = true")
    List<ForgotPassword> findByUserId(@Param("userId") Integer userId);

    @Query("select * from forgot_password where user_id = :userId and code = :code and is_valid = true")
    Optional<ForgotPassword> findByIdAndCode(@Param("userId") Integer userId, @Param("code") String code);
}
