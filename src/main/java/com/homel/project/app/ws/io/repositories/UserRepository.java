package com.homel.project.app.ws.io.repositories;

import com.homel.project.app.ws.io.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);

    UserEntity findUserByEmailVerificationToken(String token);

    @Query(value = "select * from users u where u.email_verification_status = 'true'",
            countQuery = "select count(*) from users u where u.email_verification_status = 'true'",
            nativeQuery = true)
    Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);


    @Query(value = "select * from users u where u.first_name = ?1",
    nativeQuery = true)
    List<UserEntity> findUserByFirstName(String firstName);
}
